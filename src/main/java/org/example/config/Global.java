package org.example.config;

import org.example.rule.Root;
import org.example.rule.Rule;
import org.example.rule.Sink;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.HashSet;
import java.util.Set;

public class Global {
    public static Root rule;
    public static Graph<String, DefaultEdge> cg;
    public static Set<String> sinks;

    public static Set<String> getAllSinkSignature(){
        Set<String> sinks = new HashSet<>();
        for(Rule sinkRule : rule.rules){
            if (sinkRule.name.equals("sql_injection")) {
                continue;
            }
            for(Sink sink : sinkRule.sinks){
                sinks.add(sink.expression);
            }
        }
        return sinks;
    }
}
