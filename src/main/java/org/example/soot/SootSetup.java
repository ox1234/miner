package org.example.soot;

import org.example.config.Global;
import org.example.util.ClassUtil;
import org.example.util.Log;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

// SootSetup 负责所有Soot的参数设置和Soot类的加载路径
public class SootSetup {
    // sourceTypeSpecifier 用来设置哪些类是application class以及library class
    private final SourceTypeSpecifier sourceTypeSpecifier;
    private final TargetHandler targetHandler;

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
        Path targetPath = Paths.get(target);
        if (!targetHandler.canHandle(targetPath)) {
            throw new Exception(String.format("%s target handler not support this target", targetHandler.getClass().getName()));
        }

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
        Scene.v().loadNecessaryClasses();
        for (SootClass sootClass : Scene.v().getClasses()) {
            SourceType sourceType = sourceTypeSpecifier.getClassSourceType(sootClass);
            ClassUtil.setClassType(sootClass, sourceType);
        }
        Log.info("load %d app classes, %d library classes, %d phantom classes",
                Scene.v().getApplicationClasses().size(),
                Scene.v().getLibraryClasses().size(),
                Scene.v().getPhantomClasses().size());
    }

    // 初始化方法
    public void initialize(String target) throws Exception {
        Log.info("initialize soot environment with %s path", target);
        // set soot options
        setOptions();
        setScanTarget(target);

        // load all classes
        Log.info("loading classes into soot Scene......");
        loadSootClasses();
    }
}
