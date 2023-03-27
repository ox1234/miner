package org.example;

import org.example.config.NodeRepository;
import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.flow.FlowEngine;
import org.example.soot.SootHelper;
import org.example.soot.SootSetup;
import org.example.soot.impl.FatJarHandler;
import org.example.util.Log;
import soot.Hierarchy;
import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/*

func (){
    this.xxx = xxx;
}
 */

public class Main {
    public static void main(String[] args) throws Exception {
        // 配置Soot运行环境，分析对应jar包的所有类
        SootSetup setup = new SootSetup(new FatJarHandler());
        setup.initialize("/Users/bytedance/workplace/java/demo1/target/demo1-0.0.1-SNAPSHOT.jar");
//        setup.initialize("/Users/bytedance/workplace/java/java-sec-code/target/java-sec-code-1.0.0.jar");

        // initialize analyze engine and do analysis
        Hierarchy hierarchy = SootHelper.buildHierarchy();
        CallGraph callGraph = SootHelper.buildCallGraph();
        Engine engine = new Engine(hierarchy, callGraph);
        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();

        // import call graph and intra analysis result to neo4j

//        Neo4jService service = new Neo4jService(callGraph, analyzedMethodSet);
//        Set<AbstractRelation> relations = service.buildRelations();
//        service.saveRelation(relations);

        // write jimple
        setup.cleanupOutput();
        PackManager.v().writeOutput();
        Log.info("writing jimple file to soot output directory");

        FlowEngine flowEngine = new FlowEngine(analyzedMethodSet);
        Scene.v().getEntryPoints().forEach(flowEngine::traverse);

        Log.info("java code graph construct successfully");
    }
}