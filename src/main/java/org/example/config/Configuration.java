package org.example.config;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.example.config.entry.EntryManager;
import org.example.config.entry.SpringBootEntryManager;
import org.example.config.sourcesink.SourceSinkManager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Configuration {
    private static final Logger logger = LogManager.getLogger(Configuration.class);

    private static Path runtimeConfigPath;

    private static Set<String> targets;
    private static String outputPath;
    private static Set<String> entryPoints = new LinkedHashSet<>();

    private static boolean debugMode;
    private static boolean codeGraphMode;

    private static SourceSinkManager sourceSinkManager;
    private static List<EntryManager> registeredEntryManager;

    static {
        runtimeConfigPath = Paths.get("runtime_config");

        // register spring route manager
        registeredEntryManager = new ArrayList<>();
        registerRouteManager(new SpringBootEntryManager(runtimeConfigPath));

        // register source sink manager
        sourceSinkManager = new SourceSinkManager();
        sourceSinkManager.loadSinkRuleFromFile(runtimeConfigPath.resolve("sinks.json"));

        outputPath = "tmp";
    }

    public static void parseCommandLine(CommandLine commandLine) throws Exception {
        // set target
        targets = getAllTarget(commandLine.getOptionValue("target"));

        // set debug mode
        debugMode = commandLine.hasOption("debug");
        if (debugMode) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        // set codegraph mode
        codeGraphMode = commandLine.hasOption("codegraph");

        // set output path
        String outputPathArg = commandLine.getOptionValue("output");
        if (outputPathArg != null && !outputPathArg.isEmpty()) {
            outputPath = outputPathArg;
        }

        // set entry point
        entryPoints = getAllEntryPoints(commandLine.getOptionValue("entry"));
        if (entryPoints.isEmpty() && !codeGraphMode) {
            throw new ParseException("no entry point method set, please set at least one entry point");
        }

        // set runtime config
        String runtimeConfPath = commandLine.getOptionValue("conf");
        if (runtimeConfPath != null && !runtimeConfPath.isEmpty()) {
            runtimeConfigPath = Paths.get(runtimeConfPath);
        }
    }

    public static void registerRouteManager(EntryManager entryManager) {
        registeredEntryManager.add(entryManager);
    }

    private static Set<String> getAllTarget(String target) {
        return splitWithComa(target);
    }

    private static Set<String> splitWithComa(String target) {
        Set<String> targets = new LinkedHashSet<>();
        if (target == null || target.isEmpty()) {
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

    public static void setOutputPath(String outputPath) {
        Configuration.outputPath = outputPath;
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

    public static List<EntryManager> getRegisteredEntryManager() {
        return registeredEntryManager;
    }
}
