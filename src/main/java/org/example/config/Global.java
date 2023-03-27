package org.example.config;

import org.example.rule.Root;
import org.example.rule.Rule;
import org.example.rule.RuleUtil;
import org.example.rule.Sink;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

// Global 全局配置类
public class Global {
    public static Root rule;
    public static Set<String> sinks;
    public static Map<String, Sink> sinkMap = new HashMap<>();

    public static String outputPath = "tmp";
    public static String neo4jDSN = "neo4j://localhost:7687";
    public static String neo4jUser = "neo4j";
    public static String neo4jPass = "password";

    public static String sootOutputPath = "sootOutput";

    public static boolean allReachable = false;
    public static boolean onlyCG = false;

    public static Set<String> getAllSinkSignature() {
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

    static {
        try {
            FileInputStream fileInputStream = new FileInputStream("config.json");
            byte[] buf = new byte[fileInputStream.available()];
            fileInputStream.read(buf);
            Root rule = RuleUtil.getRule(new String(buf));
            Global.rule = rule;
            Global.sinks = Global.getAllSinkSignature();
            Global.rule.rules.forEach(rule1 -> rule1.sinks.forEach(sink -> sinkMap.put(sink.expression, sink)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
