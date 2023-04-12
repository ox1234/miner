package org.example.soot;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;
import org.example.tags.RouteTag;
import org.example.util.ClassUtil;
import org.example.util.MethodUtil;
import org.example.util.TagUtil;
import soot.*;
import soot.options.Options;
import soot.tagkit.AnnotationTag;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.BiConsumer;

// SootSetup 负责所有Soot的参数设置和Soot类的加载路径
public class SootSetup {
    private final Logger logger = LogManager.getLogger(SootSetup.class);
    // sourceTypeSpecifier 用来设置哪些类是application class以及library class
    private final SourceTypeSpecifier sourceTypeSpecifier;
    private final TargetHandler targetHandler;
    private Hierarchy hierarchy;

    public SootSetup(TargetHandler targetHandler) {
        sourceTypeSpecifier = new DefaultSourceTypeSpecifier();
        this.targetHandler = targetHandler;
    }

    public SootSetup(TargetHandler targetHandler, SourceTypeSpecifier specifier) {
        this.sourceTypeSpecifier = specifier;
        this.targetHandler = targetHandler;
    }

    // setScanTarget 该函数负责Soot分析的路径，支持war，jar包（会自解压压缩包）
    private void setScanTarget(String target) throws Exception {
        logger.info(String.format("will initialize soot environment with %s path", target));
        Path targetPath = Paths.get(target);
        if (!targetHandler.canHandle(targetPath)) {
            throw new Exception(String.format("%s target handler not support this target", targetHandler.getClass().getName()));
        }

        logger.info(String.format("%s target handler will collect class information", targetHandler.getClass()));
        Options.v().set_process_dir(targetHandler.getTargetClassDir(targetPath));
        Options.v().set_soot_classpath(String.join(":", targetHandler.getLibraryClassDir(targetPath)));
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
        Options.v().set_output_dir(Global.sootOutputPath);
        Options.v().set_no_writeout_body_releasing(true);

        // speed up soot
        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("jb.cp-ule", "enabled:false");
        Options.v().setPhaseOption("jb.dae", "enabled:false");

        // disable some phase
        Options.v().setPhaseOption("cg", "enabled:false");
        Options.v().setPhaseOption("jop", "enabled:false");
        Options.v().setPhaseOption("jap.baf", "enabled:false");
        Options.v().setPhaseOption("bop", "enabled:false");
        Options.v().setPhaseOption("jtp.grimp", "enabled:false");


    }

    public void cleanupOutput() {
        File file = new File(Global.sootOutputPath);
        if (file.isDirectory() && file.exists()) {
            file.delete();
        }
    }

    // 加载所有类到内存中
    private void loadSootClasses() {
        logger.info("loading classes into soot Scene......");

        Scene.v().loadNecessaryClasses();
        for (SootClass sootClass : Scene.v().getClasses()) {
            SourceType sourceType = sourceTypeSpecifier.getClassSourceType(sootClass);
            ClassUtil.setClassType(sootClass, sourceType);
        }
        logger.info(String.format("load %d app classes, %d library classes, %d phantom classes",
                Scene.v().getApplicationClasses().size(),
                Scene.v().getLibraryClasses().size(),
                Scene.v().getPhantomClasses().size()));
    }

    private void setupEntryPoints() {
        List<SootMethod> entryMethods = new ArrayList<>();
        getRouteMethods(Scene.v().getApplicationClasses()).forEach((sootMethod, routes) -> {
            sootMethod.addTag(new RouteTag(routes));
            entryMethods.add(sootMethod);
        });
        logger.info(String.format("collect %d route methods", entryMethods.size()));
        Scene.v().setEntryPoints(entryMethods);
    }

    private void runPacks() {
        logger.info("running soot phases");
        PackManager.v().runPacks();
    }

    // 初始化方法
    public void initialize(String target) throws Exception {
        StopWatch sw = new StopWatch();

        // set soot options
        setOptions();
        setScanTarget(target);

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

    public Map<SootMethod, Set<String>> getRouteMethods(Collection<SootClass> classes) {
        logger.info(String.format("start route collect in %d classes", classes.size()));
        Map<SootMethod, Set<String>> routeMethods = new LinkedHashMap<>();
        for (SootClass sootClass : classes) {
            for (AnnotationTag annotationTag : TagUtil.getClassAnnotation(sootClass)) {
                if (TagUtil.isSpringControllerAnnotation(annotationTag)) {
                    for (SootMethod sootMethod : sootClass.getMethods()) {
                        if (MethodUtil.isRouteMethod(sootMethod)) {
                            logger.info(String.format("find %s method is route method with %s route path", sootMethod.getSignature(), String.join(",", TagUtil.getMethodRoutePath(sootMethod))));
                            if (!routeMethods.containsKey(sootMethod)) {
                                routeMethods.put(sootMethod, new HashSet<>());
                            }
                            routeMethods.get(sootMethod).addAll(TagUtil.getMethodRoutePath(sootMethod));
                        }
                    }
                }
            }
        }
        logger.info(String.format("get %d route methods in project", routeMethods.size()));
        return routeMethods;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }
}
