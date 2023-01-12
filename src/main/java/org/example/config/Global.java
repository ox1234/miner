package org.example.config;

import org.example.core.basic.Node;
import org.example.rule.Root;
import org.example.rule.Rule;
import org.example.rule.RuleUtil;
import org.example.rule.Sink;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import soot.SootMethod;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Global {
    public static Root rule;
    public static Set<String> sinks;

    public static String outputPath = "tmp";
    public static String neo4jDSN = "neo4j://localhost:7687";
    public static String neo4jUser = "neo4j";
    public static String neo4jPass = "password";

    public static boolean allReachable = true;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
