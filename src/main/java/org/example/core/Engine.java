package org.example.core;

import org.example.config.Global;
import org.example.core.visitor.StmtVisitor;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.service.Neo4jService;
import org.example.tags.LocationTag;
import org.example.util.ClassUtil;
import org.example.util.Log;
import org.example.util.MethodUtil;
import soot.*;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.AnnotationTag;
import soot.util.Chain;

import java.util.*;
import java.util.function.Consumer;

public class Engine {
    private Hierarchy hierarchy;
    private CallGraph callGraph;

    public Engine(Hierarchy hierarchy, CallGraph callGraph) {
        this.hierarchy = hierarchy;
        this.callGraph = callGraph;
    }

    // doIntraProcedureAnalysis 对给定的SootMethod，进行过程内的指针分析
    public IntraAnalyzedMethod doIntraProcedureAnalysis(SootMethod method) {
        Log.info("do intra procedure analysis on %s", method.getSignature());
        if (!method.isConcrete()) {
            return null;
        }

        Body body = method.retrieveActiveBody();

        int order = 1;
        for (Unit unit : body.getUnits()) {
            unit.addTag(new LocationTag(method, order, unit));
            StmtVisitor stmtVisitor = StmtVisitor.getInstance(method, unit);
            unit.apply(stmtVisitor);
            order++;
        }
        return new IntraAnalyzedMethod(method);
    }

    public Set<IntraAnalyzedMethod> extractPointRelation() {
        Set<IntraAnalyzedMethod> intraAnalyzedMethods = new HashSet<>();
        Set<String> visitedMethods = new HashSet<>();
        callGraph.forEach(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                SootMethod srcMethod = edge.src();
                SootMethod tgtMethod = edge.tgt();
                if (needDoIntraAnalysis(srcMethod)) {
                    addAnalyzedMethod(doIntraProcedureAnalysis(srcMethod));
                }
                if (needDoIntraAnalysis(tgtMethod)) {
                    addAnalyzedMethod(doIntraProcedureAnalysis(tgtMethod));
                }
                visitedMethods.add(srcMethod.getSignature());
                visitedMethods.add(tgtMethod.getSignature());
            }

            private boolean needDoIntraAnalysis(SootMethod sootMethod) {
                return !visitedMethods.contains(sootMethod.getSignature()) && sootMethod.getDeclaringClass().isApplicationClass();
            }

            private void addAnalyzedMethod(IntraAnalyzedMethod analyzedMethod) {
                if (analyzedMethod == null) {
                    return;
                }
                intraAnalyzedMethods.add(analyzedMethod);
            }
        });
        return intraAnalyzedMethods;
    }
}
