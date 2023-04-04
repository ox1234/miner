package org.example.soot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;
import org.example.util.ClassUtil;
import org.example.util.MethodUtil;
import org.example.util.TagUtil;
import soot.*;
import soot.options.Options;
import soot.tagkit.AnnotationTag;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        Options.v().set_whole_program(true);
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_whole_shimple(true);
        Options.v().set_output_format(Options.output_format_shimp);
        Options.v().set_keep_line_number(true);
        Options.v().set_keep_offset(true);
        Options.v().set_output_dir(Global.sootOutputPath);
        Options.v().set_no_writeout_body_releasing(true);

        Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().setPhaseOption("cg", "spark:false");
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
        Scene.v().setEntryPoints(getRouteMethods(Scene.v().getApplicationClasses()));
        logger.info(String.format("set %d entry points", Scene.v().getEntryPoints().size()));
    }

    private void runPacks() {
        logger.info("running soot phases");
        PackManager.v().runPacks();
    }

    // 初始化方法
    public void initialize(String target) throws Exception {
        // set soot options
        setOptions();
        setScanTarget(target);

        // load all classes
        loadSootClasses();

        // set up entry points
        setupEntryPoints();

        // run packs
        runPacks();

        // build hierarchy
        hierarchy = buildHierarchy();
    }

    public Hierarchy buildHierarchy() {
        logger.info("building class hierarchy");
        return Scene.v().getActiveHierarchy();
    }

    public List<SootMethod> getRouteMethods(Collection<SootClass> classes) {
        List<SootMethod> routeMethods = new ArrayList<>();
        for (SootClass sootClass : classes) {
            logger.info(String.format("searching %s class route methods in application classes", sootClass.getName()));
            for (AnnotationTag annotationTag : TagUtil.getClassAnnotation(sootClass)) {
                if (TagUtil.isSpringControllerAnnotation(annotationTag)) {
                    for (SootMethod sootMethod : sootClass.getMethods()) {
                        if (MethodUtil.isRouteMethod(sootMethod)) {
                            logger.info(String.format("find %s method is route method with %s route path", sootMethod.getSignature(), String.join(",", TagUtil.getMethodRoutePath(sootMethod))));
                            routeMethods.add(sootMethod);
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
