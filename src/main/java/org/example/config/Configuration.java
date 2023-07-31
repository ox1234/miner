package org.example.config;

import org.apache.commons.cli.CommandLine;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.example.core.Engine;
import org.example.rule.Root;
import org.example.rule.Rule;
import org.example.rule.RuleUtil;
import org.example.rule.Sink;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Configuration {
    private static final Logger logger = LogManager.getLogger(Configuration.class);

    private static String target;
    private static String outputPath;

    private static Root rule;
    private static Set<String> sinks;
    private static Map<String, Sink> sinkMap;

    private static boolean debugMode;
    private static boolean codeGraphMode;

    public static void initialize(CommandLine commandLine) {
        target = commandLine.getOptionValue("target");
        sinkMap = new HashMap<>();
        debugMode = commandLine.hasOption("debug");
        codeGraphMode = commandLine.hasOption("codegraph");
        outputPath = commandLine.getOptionValue("output");

        if (debugMode) {
            Configurator.setRootLevel(Level.DEBUG);
        }

        try (FileInputStream fileInputStream = new FileInputStream("config.json")) {
            byte[] buf = new byte[fileInputStream.available()];
            fileInputStream.read(buf);
            rule = RuleUtil.getRule(new String(buf));
            sinks = getAllSinkSignature();
            rule.rules.forEach(rule1 -> rule1.sinks.forEach(sink -> sinkMap.put(sink.expression, sink)));
        } catch (Exception e) {
            logger.info(String.format("read source sink rules fail: %s", e.getMessage()));
        }
    }

    private static Set<String> getAllSinkSignature() {
        Set<String> sinks = new HashSet<>();
        for (Rule sinkRule : rule.rules) {
            if (sinkRule.name.equals("sql_injection")) {
                continue;
            }
            for (Sink sink : sinkRule.sinks) {
                sinks.add(sink.expression);
            }
        }
        return sinks;
    }

    public static String getTarget() {
        return target;
    }

    public static String getOutputPath() {
        return outputPath;
    }

    public static Root getRule() {
        return rule;
    }

    public static Set<String> getSinks() {
        return sinks;
    }

    public static Map<String, Sink> getSinkMap() {
        return sinkMap;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static boolean isCodeGraphMode() {
        return codeGraphMode;
    }
}
