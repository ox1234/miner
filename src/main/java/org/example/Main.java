package org.example;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Configuration;
import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.flow.FlowEngine;
import org.example.soot.SootSetup;
import soot.Hierarchy;
import soot.PackManager;
import soot.Scene;

import java.io.File;
import java.util.Map;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        // string args
        options.addRequiredOption("t", "target", true, "target to scan");
        options.addOption(Option.builder("output").argName("path").hasArg().desc("tool write to output").build());
        options.addOption(Option.builder("entry").argName("method").hasArg().desc("entry point method to start flow").build());
        options.addOption(Option.builder("conf").argName("path").hasArg().desc("runtime config directory").build());
        // bool args
        options.addOption(Option.builder("debug").desc("enable debug mode to log more info").build());
        options.addOption(Option.builder("codegraph").desc("run as code graph client").build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            Configuration.initialize(commandLine);
        } catch (ParseException e) {
            System.err.println("Parsing command line failed.  Reason: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("miner", options);
            return;
        }

        // delete previous scan tmp path and soot output path
        FileUtils.deleteDirectory(new File(Configuration.getOutputPath()));

        // setup soot environment
        SootSetup setup = new SootSetup();
        setup.initialize(Configuration.getTargets());

        // initialize analyze engine and do analysis
        Hierarchy hierarchy = setup.getHierarchy();
        Engine engine = new Engine(hierarchy);
        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();

        // do inter analysis based on intra analysis result
        FlowEngine flowEngine = new FlowEngine(analyzedMethodSet);
        Scene.v().getEntryPoints().forEach(flowEngine::doAnalysis);

        logger.info("java code graph construct successfully");
    }
}