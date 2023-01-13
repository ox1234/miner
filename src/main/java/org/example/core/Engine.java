package org.example.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;
import org.example.core.visitor.StmtVisitor;
import org.example.neo4j.node.method.Method;
import org.example.neo4j.service.MethodService;
import org.example.util.ClassUtil;
import org.example.util.MethodUtil;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.tagkit.AnnotationTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Engine {
    Logger logger = LogManager.getRootLogger();

    private Hierarchy hierarchy;
    private CallGraph callGraph;

    public Hierarchy getHierarchy() {
        logger.info("getting class hierarchy");
        if (hierarchy == null) {
            logger.info("class hierarchy is not active, will build");
            buildHierarchy();
            logger.info("class hierarchy build finished");
        }
        return hierarchy;
    }

    public CallGraph getCallGraph() {
        logger.info("getting call graph");
        if (callGraph == null) {
            logger.info("call graph is not active, will build");
            buildCallGraph();
            logger.info("call graph build finished");
        }
        return callGraph;
    }

    public void doIntraProcedureAnalysis(SootMethod method) {
        logger.info(String.format("do intra procedure analysis on %s", method.getSignature()));
        Body body = method.retrieveActiveBody();
        for (Unit unit : body.getUnits()) {
            StmtVisitor stmtVisitor = StmtVisitor.getInstance(method, unit);
            unit.apply(stmtVisitor);
        }
    }

    public void extractPointRelation() {
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
            logger.info(String.format("extracting %s soot class point relation", sootClass.getName()));
            for (SootMethod sootMethod : sootClass.getMethods()) {
                doIntraProcedureAnalysis(sootMethod);
            }
        }
    }


    private void buildCallGraph() {
        if (!Global.allReachable) {
            List<SootMethod> routeMethods = getRouteMethods();
            Scene.v().setEntryPoints(routeMethods);
        }
        PackManager.v().runPacks();
        callGraph = Scene.v().getCallGraph();
    }

    private void buildHierarchy() {
        hierarchy = Scene.v().getActiveHierarchy();
    }

    private List<SootMethod> getRouteMethods() {
        List<SootMethod> routeMethods = new ArrayList<>();
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
//            System.out.printf("%s class has %d method%n", sootClass.getName(), sootClass.getMethods().size());
            for (AnnotationTag annotationTag : ClassUtil.getClassAnnotation(sootClass)) {
                if (ClassUtil.isSpringControllerAnnotation(annotationTag)) {
                    for (SootMethod sootMethod : sootClass.getMethods()) {
                        if (MethodUtil.isRouteMethod(sootMethod)) {
                            logger.info(String.format("find %s method is route method", sootMethod.getSignature()));
                            routeMethods.add(sootMethod);
                        }
                    }
                }
            }
        }
        logger.info(String.format("get %d route methods in project", routeMethods.size()));
        return routeMethods;
    }

    public void importCallGraphToNeo4j(CallGraph callGraph) {
        logger.info("start filter application call edge");
        Map<String, Method> neo4jEntities = new HashMap<>();
        callGraph.forEach(edge -> {
            if (edge.tgt() == null) {
                return;
            }
            if (!edge.src().getDeclaringClass().isApplicationClass()) {
                return;
            }

            String srcSig = edge.src().getSignature();
            Method srcMethod = null;
            if (neo4jEntities.containsKey(srcSig)) {
                srcMethod = neo4jEntities.get(srcSig);
            } else {
                srcMethod = Method.getInstance(edge.src());
                neo4jEntities.put(edge.src().getSignature(), srcMethod);
            }

            srcMethod.appendCallee(Method.getInstance(edge.tgt()), edge.srcStmt());
        });

        MethodService methodService = new MethodService();
        logger.info("importing to neo4j database");
        neo4jEntities.forEach((s, method) -> {
            methodService.createMethodNode(method);
        });
    }
}
