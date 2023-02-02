package org.example;

import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.neo4j.relation.AbstractRelation;
import org.example.neo4j.service.Neo4jService;
import org.example.soot.SootHelper;
import org.example.soot.SootSetup;
import org.example.util.Log;
import soot.Hierarchy;
import soot.PackManager;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.Set;

public class Main {
    public static void main(String[] args) throws Exception {
        // 配置Soot运行环境，分析对应jar包的所有类
        SootSetup setup = new SootSetup();
        setup.initialize("/Users/bytedance/workplace/java/demo1/target/demo1-0.0.1-SNAPSHOT.jar");
//        setup.initialize("/Users/bytedance/workplace/java/demo1/testjars/webgoat-2023.3.jar");

        // initialize analyze engine and do analysis
        Hierarchy hierarchy = SootHelper.buildHierarchy();
        CallGraph callGraph = SootHelper.buildCallGraph();
        Engine engine = new Engine(hierarchy, callGraph);
        Set<IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();

        // import call graph and intra analysis result to neo4j
        Neo4jService service = new Neo4jService(callGraph, analyzedMethodSet);
        Set<AbstractRelation> relations = service.buildRelations();
        service.saveRelation(relations);

        // write jimple
        setup.cleanupOutput();
        PackManager.v().writeOutput();
        Log.info("writing jimple file to soot output directory");
        Log.info("java code graph construct successfully");
    }
}