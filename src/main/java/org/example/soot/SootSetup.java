package org.example.soot;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;
import org.example.exception.NoSootTargetException;
import org.example.util.ClassUtil;
import org.example.util.JarUtil;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SootSetup {
    Logger logger = LogManager.getRootLogger();

    private SourceTypeSpecifier sourceTypeSpecifier;

    public SootSetup() {
        sourceTypeSpecifier = new DefaultSourceTypeSpecifier();
    }

    public SootSetup(SourceTypeSpecifier specifier) {
        this.sourceTypeSpecifier = specifier;
    }

    private void setScanTarget(String target) throws Exception {
        Path targetPath = Paths.get(target);
        String targetName = targetPath.toFile().getName();

        Path outputPath = null;
        // check file is jar
        if (targetName.endsWith(".jar") || targetName.endsWith(".war")) {
            outputPath = Paths.get(Global.outputPath).resolve(DigestUtils.sha1Hex(targetName));
            if (!outputPath.toFile().exists()) {
                outputPath.toFile().mkdirs();
            }
            JarUtil.extractJar(targetPath, outputPath);
        }

        Path appClassPath = outputPath.resolve("BOOT-INF/classes");
        Path libClassPath = outputPath.resolve("BOOT-INF/lib");

        Collection<File> libJars = FileUtils.listFiles(libClassPath.toFile(), new String[]{"jar"}, true);

        Options.v().set_process_dir(Collections.singletonList(appClassPath.toFile().getAbsolutePath()));
        Options.v().set_soot_classpath(getLibClassPathStr(libJars));
    }

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


        if (Global.allReachable) {
            Options.v().setPhaseOption("cg", "all-reachable:true");
        }
    }

    private void loadSootClasses() {
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

    public void initialize(String target) throws Exception {
        // set soot options
        setOptions();
        setScanTarget(target);

        // run soot
        loadSootClasses();
    }

    public String getLibClassPathStr(Collection<File> jars) {
        List<String> files = new ArrayList<>();
        for (File jar : jars) {
            files.add(jar.getAbsolutePath());
        }
        return String.join(":", files);
    }
}
