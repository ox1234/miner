package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.Engine;
import org.example.soot.SootSetup;
import soot.jimple.toolkits.callgraph.CallGraph;

public class Main {
    public static void main(String[] args) throws Exception {
        SootSetup setup = new SootSetup();
        setup.initialize("/Users/bytedance/workplace/java/demo1/target/demo1-0.0.1-SNAPSHOT.jar");
//        setup.initialize("/Users/bytedance/workplace/java/demo1/testjars/webgoat-2023.3.jar");

        Engine engine = new Engine();
        engine.extractPointRelation();
        System.out.println("finish");
    }
}