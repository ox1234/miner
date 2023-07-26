package org.example;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.example.config.Global;
import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.processor.bytedance.CodeGraphHook;
import org.example.flow.FlowEngine;
import org.example.soot.SootSetup;
import org.example.soot.impl.FatJarHandler;
import org.example.util.ClassUtil;
import org.example.util.FileUtil;
import soot.Hierarchy;
import soot.PackManager;
import soot.Scene;

import java.io.File;
import java.util.Map;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (Global.debug) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        // delete previous scan tmp path and soot output path
        FileUtil.deleteDirectory(new File(Global.outputPath));
        FileUtils.deleteDirectory(new File(Global.sootOutputPath));

        // if bytedance mode, will download source code from SCM
        if (Global.bytedanceMode) {
            String repoName = "";
            String branch = "";
        }


        String target = "/Users/bytedance/workplace/java/java-sec-code/target/java-sec-code-1.0.0.jar";

        // setup soot environment
        SootSetup setup = new SootSetup(new FatJarHandler());
//        setup.initialize("/Users/bytedance/workplace/java/demo1/target/demo1-0.0.1-SNAPSHOT.jar");
        setup.initialize(target);
//        setup.initialize("/Users/bytedance/workplace/java/java-sec-code-origin/target/java-sec-code-1.0.0.jar");

        // initialize analyze engine and do analysis
        Hierarchy hierarchy = setup.getHierarchy();
        Engine engine = new Engine(hierarchy);

        engine.addEngineHook(new CodeGraphHook(target));

        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();

        // write jimple
        if (Global.debug) {
            PackManager.v().writeOutput();
            logger.info("intra analysis finish, writing jimple file to soot output directory");
        }

        // do inter analysis based on intra analysis result
        FlowEngine flowEngine = new FlowEngine(analyzedMethodSet);
        Scene.v().getEntryPoints().forEach(flowEngine::doAnalysis);

//        FlowEngine.printRouteTable();
        logger.info("java code graph construct successfully");
    }
}