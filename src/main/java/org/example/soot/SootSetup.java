package org.example.soot;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.entry.EntryManager;
import org.example.config.entry.IEntry;
import org.example.config.entry.RouterEntry;
import org.example.config.Configuration;
import org.example.soot.impl.AchieveHandler;
import org.example.soot.impl.DirectoryHandler;
import org.example.soot.impl.FatJarHandler;
import org.example.soot.impl.SingleClassHandler;
import org.example.util.ClassUtil;
import soot.*;
import soot.options.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// SootSetup 负责所有Soot的参数设置和Soot类的加载路径
public class SootSetup {
    private final Logger logger = LogManager.getLogger(SootSetup.class);
    // sourceTypeSpecifier 用来设置哪些类是application class以及library class
    private SourceTypeSpecifier sourceTypeSpecifier;
    private final List<TargetHandler> registeredHandlers;
    private Hierarchy hierarchy;
    private List<String> applicationDirs;
    private List<String> libraryDirs;
    private Set<IEntry> entries;

    public SootSetup() {
        sourceTypeSpecifier = new DefaultSourceTypeSpecifier();
        this.registeredHandlers = new ArrayList<>();
        this.applicationDirs = new ArrayList<>();
        this.libraryDirs = new ArrayList<>();

        // register built in target handler
        this.registerTargetHandler(new SingleClassHandler());
        this.registerTargetHandler(new DirectoryHandler());
        this.registerTargetHandler(new FatJarHandler());
        this.registerTargetHandler(new AchieveHandler());
    }

    public void setSourceTypeSpecifier(SourceTypeSpecifier specifier) {
        this.sourceTypeSpecifier = specifier;
    }

    public void registerTargetHandler(TargetHandler targetHandler) {
        registeredHandlers.add(targetHandler);
    }

    // setScanTarget 该函数负责Soot分析的路径，支持war，jar包（会自解压压缩包）
    private void setScanTarget(String target) throws Exception {
        logger.info(String.format("will parseCommandLine soot environment with %s path", target));
        Path targetPath = Paths.get(target);
        for (TargetHandler registeredHandler : registeredHandlers) {
            if (registeredHandler.canHandle(targetPath)) {
                logger.info(String.format("%s target handler will collect class information", registeredHandler.getClass()));
                applicationDirs.addAll(registeredHandler.getTargetClassDir(targetPath));
                libraryDirs.addAll(registeredHandler.getLibraryClassDir(targetPath));
                return;
            }
        }
        throw new Exception(String.format("no registered target handler can handle %s target", target));
    }

    // setOptions Soot的phase配置
    private void setOptions() {
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_allow_phantom_elms(true);
        Options.v().set_whole_program(false);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_whole_shimple(true);
        Options.v().set_output_format(Options.output_format_shimp);
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
        Options.v().set_output_dir(Paths.get(Configuration.getOutputPath()).resolve("soot_output").toString());
        Options.v().set_no_writeout_body_releasing(true);

        // speed up soot
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jb.cp-ule", "enabled:false");
        Options.v().setPhaseOption("jb.dae", "enabled:false");
        Options.v().setPhaseOption("tag.ln", "enabled:true");

        // disable some phase
        Options.v().setPhaseOption("cg", "enabled:false");
        Options.v().setPhaseOption("jop", "enabled:false");
        Options.v().setPhaseOption("jap.baf", "enabled:false");
        Options.v().setPhaseOption("bop", "enabled:false");
        Options.v().setPhaseOption("jtp.grimp", "enabled:false");


    }

    // 加载所有类到内存中
    private void loadSootClasses() {
        logger.info("loading classes into soot Scene......");

        Scene.v().loadNecessaryClasses();
        for (SootClass sootClass : Scene.v().getClasses()) {
            SourceType sourceType = sourceTypeSpecifier.getClassSourceType(sootClass);
            ClassUtil.setClassType(sootClass, sourceType);
        }
        logger.info(String.format("load %d app classes, %d library classes, %d phantom classes", Scene.v().getApplicationClasses().size(), Scene.v().getLibraryClasses().size(), Scene.v().getPhantomClasses().size()));
    }

    private void setupEntryPoints() {
        List<SootMethod> entryMethods = new ArrayList<>();
        Set<String> entryPoints = Configuration.getEntryPoints();
        if (entryPoints.isEmpty()) {
            entries = getEntries(Scene.v().getApplicationClasses());
            entries.forEach(perEntry -> {
                entryMethods.add(perEntry.entryMethod());
            });
        } else {
            entryPoints.forEach(sig -> {
                SootMethod entryMethod = Scene.v().grabMethod(sig);
                if (entryMethod != null) {
                    entryMethods.add(entryMethod);
                }
            });
        }
        logger.info(String.format("collect %d entry point methods", entryMethods.size()));
        Scene.v().setEntryPoints(entryMethods);
    }

    private void runPacks() {
        logger.info("running soot phases");
        PackManager.v().runPacks();
    }

    // 初始化方法
    public void initialize(Set<String> targets) throws Exception {
        StopWatch sw = new StopWatch();

        // reset soot environment
        G.reset();

        // set soot options
        setOptions();

        for (String target : targets) {
            setScanTarget(target);
        }

        // set application and classpath
        Options.v().set_process_dir(applicationDirs);
        logger.info(String.format("set soot process dir with: %s", String.join(",", applicationDirs)));
        Options.v().set_soot_classpath(String.join(":", libraryDirs));
        logger.info(String.format("set soot class path with: %s", String.join(":", libraryDirs)));

        // load all classes
        sw.reset();
        sw.start();
        loadSootClasses();
        sw.stop();
        logger.info(String.format("load classes into Scene cost %s time", sw));

        // set up entry points
        setupEntryPoints();

        // run packs
        sw.reset();
        sw.start();
        runPacks();
        sw.stop();
        logger.info(String.format("run soot packs cost %s time", sw));


        // build hierarchy
        hierarchy = buildHierarchy();
    }

    public Hierarchy buildHierarchy() {
        logger.info("building class hierarchy");
        return Scene.v().getActiveHierarchy();
    }

    private Set<IEntry> getEntries(Collection<SootClass> classes) {
        logger.info(String.format("start route collect in %d classes", classes.size()));
        List<EntryManager> entryManagers = Configuration.getRegisteredEntryManager();
        Set<IEntry> entries = new LinkedHashSet<>();
        entryManagers.forEach(entryManager -> classes.forEach(sootClass -> {
            entries.addAll(entryManager.getEntry(sootClass));
        }));
        logger.info(String.format("get %d route methods in project", entries.size()));
        return entries;
    }

    public Set<IEntry> getEntries() {
        return entries;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }
}
