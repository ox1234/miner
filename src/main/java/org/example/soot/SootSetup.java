package org.example.soot;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.example.config.Global;
import org.example.util.ClassUtil;
import org.example.util.JarUtil;
import org.example.util.Log;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// SootSetup 负责所有Soot的参数设置和Soot类的加载路径
public class SootSetup {
    // sourceTypeSpecifier 用来设置哪些类是application class以及library class
    private SourceTypeSpecifier sourceTypeSpecifier;

    public SootSetup() {
        sourceTypeSpecifier = new DefaultSourceTypeSpecifier();
    }

    public SootSetup(SourceTypeSpecifier specifier) {
        this.sourceTypeSpecifier = specifier;
    }

    // setScanTarget 该函数负责Soot分析的路径，支持war，jar包（会自解压压缩包）
    private void setScanTarget(String target) throws Exception {
        Path targetPath = Paths.get(target);
        String targetName = targetPath.toFile().getName();

        Path outputPath = null;
        // check file is jar
        if (targetName.endsWith(".jar") || targetName.endsWith(".war")) {
            outputPath = Paths.get(Global.outputPath).resolve(DigestUtils.sha1Hex(targetName));
            Log.info("target is a packed file(jar/war), will extract inner class in %s", outputPath);
            if (!outputPath.toFile().exists()) {
                outputPath.toFile().mkdirs();
            } else {
                outputPath.toFile().delete();
                outputPath.toFile().mkdirs();
            }
            JarUtil.extractJar(targetPath, outputPath);
        }

        assert outputPath != null;
        Path appClassPath = outputPath.resolve("BOOT-INF/classes");
        Path libClassPath = outputPath.resolve("BOOT-INF/lib");

        Collection<File> libJars = FileUtils.listFiles(libClassPath.toFile(), new String[]{"jar"}, true);

        Options.v().set_process_dir(Collections.singletonList(appClassPath.toFile().getAbsolutePath()));
        Options.v().set_soot_classpath(getLibClassPathStr(libJars));
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
        Options.v().setPhaseOption("cg.cha", "on");

        if (Global.allReachable) {
            Options.v().setPhaseOption("cg", "all-reachable:true");
        }
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

    private String getLibClassPathStr(Collection<File> jars) {
        List<String> files = new ArrayList<>();
        for (File jar : jars) {
            files.add(jar.getAbsolutePath());
        }
        return String.join(":", files);
    }
}
