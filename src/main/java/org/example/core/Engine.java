package org.example.core;

import org.example.config.Global;
import org.example.core.visitor.StmtVisitor;
import org.example.neo4j.node.Method;
import org.example.neo4j.service.MethodService;
import org.example.util.ClassUtil;
import org.example.util.MethodUtil;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.tagkit.AnnotationTag;
import soot.util.Switch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Engine {
    private Hierarchy hierarchy;
    private CallGraph callGraph;

    public Hierarchy getHierarchy() {
        if (hierarchy == null) {
            buildHierarchy();
        }
        return hierarchy;
    }

    public CallGraph getCallGraph() {
        if (callGraph == null) {
            buildCallGraph();
        }
        return callGraph;
    }

    public void doIntraProcedureAnalysis(SootMethod method) {
        Body body = method.retrieveActiveBody();
        for (Unit unit : body.getUnits()) {
            StmtVisitor stmtVisitor = StmtVisitor.getInstance(method, unit);
            unit.apply(stmtVisitor);
        }
    }

    public void extractPointRelation() {
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
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
                            routeMethods.add(sootMethod);
                        }
                    }
                }
            }
        }
        return routeMethods;
    }

    public void importToNeo4j(CallGraph callGraph) {
        Map<String, Method> neo4jEntites = new HashMap<>();
        callGraph.forEach(edge -> {
            if (edge.tgt() == null) {
                return;
            }
            if (!edge.src().getDeclaringClass().isApplicationClass()) {
                return;
            }

            String srcSig = edge.src().getSignature();
            Method srcMethod = null;
            if (neo4jEntites.containsKey(srcSig)) {
                srcMethod = neo4jEntites.get(srcSig);
            } else {
                srcMethod = Method.getInstance(edge.src());
                neo4jEntites.put(edge.src().getSignature(), srcMethod);
            }
            srcMethod.appendCallee(Method.getInstance(edge.tgt()));
        });

        MethodService methodService = new MethodService();
        neo4jEntites.forEach((s, method) -> {
            methodService.createMethodNode(method);
        });
    }
}
