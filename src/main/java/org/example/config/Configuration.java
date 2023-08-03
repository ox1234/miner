package org.example.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.example.config.router.RouteManager;
import org.example.config.router.SprintBootRouteManager;
import org.example.config.sourcesink.SourceSinkManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Configuration {
    private static final Logger logger = LogManager.getLogger(Configuration.class);

    private static Path runtimeConfigPath;

    private static Set<String> targets;
    private static String outputPath;
    private static Set<String> entryPoints;

    private static boolean debugMode;
    private static boolean codeGraphMode;

    private static SourceSinkManager sourceSinkManager;
    private static List<RouteManager> registeredRouteManager;

    public static void initialize(CommandLine commandLine) throws Exception {
        entryPoints = new LinkedHashSet<>();

        targets = getAllTarget(commandLine.getOptionValue("target"));
        debugMode = commandLine.hasOption("debug");
        if (debugMode) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        codeGraphMode = commandLine.hasOption("codegraph");

        outputPath = commandLine.getOptionValue("output");
        if (outputPath == null || outputPath.equals("")) {
            outputPath = "tmp";
        }

        entryPoints = getAllEntryPoints(commandLine.getOptionValue("entry"));
        if (entryPoints.size() == 0 && !codeGraphMode) {
            throw new ParseException("no entry point method set, please set at least one entry point");
        }

        String runtimeConfPath = commandLine.getOptionValue("conf");
        if (runtimeConfPath == null || runtimeConfPath.equals("")) {
            runtimeConfPath = "runtime_config";
        }
        runtimeConfigPath = Paths.get(runtimeConfPath);


        // register spring route manager
        registeredRouteManager = new ArrayList<>();
        registerRouteManager(new SprintBootRouteManager(runtimeConfigPath));

        // register source sink manager
        sourceSinkManager = new SourceSinkManager();
        sourceSinkManager.loadSinkRuleFromFile(runtimeConfigPath.resolve("sinks.json"));
    }

    public static void registerRouteManager(RouteManager routeManager) {
        registeredRouteManager.add(routeManager);
    }

    private static Set<String> getAllTarget(String target) {
        return splitWithComa(target);
    }

    private static Set<String> splitWithComa(String target) {
        Set<String> targets = new LinkedHashSet<>();
        if (target == null || target.equals("")) {
            return targets;
        }
        Arrays.stream(target.split(",")).forEach(s -> targets.add(s.trim()));
        return targets;
    }

    private static Set<String> getAllEntryPoints(String entry) {
        return splitWithComa(entry);
    }

    public static Set<String> getTargets() {
        return targets;
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static boolean isCodeGraphMode() {
        return codeGraphMode;
    }

    public static Set<String> getEntryPoints() {
        return entryPoints;
    }

    public static SourceSinkManager getSourceSinkManager() {
        return sourceSinkManager;
    }

    public static List<RouteManager> getRegisteredRouteManager() {
        return registeredRouteManager;
    }
}
