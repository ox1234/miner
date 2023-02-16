package org.example.core;

import org.example.core.basic.Site;
import org.example.core.visitor.StmtVisitor;
import org.example.tags.LocationTag;
import org.example.util.Log;
import org.example.util.TagUtil;
import soot.*;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;

import java.util.*;
import java.util.function.Consumer;

public class Engine {
    private Hierarchy hierarchy;
    private CallGraph callGraph;
    private Set<SootClass> patchedClasses;

    public Engine(Hierarchy hierarchy, CallGraph callGraph) {
        this.hierarchy = hierarchy;
        this.callGraph = callGraph;
        this.patchedClasses = new HashSet<>();
    }

    // doIntraProcedureAnalysis 对给定的SootMethod，进行过程内的指针分析
    public IntraAnalyzedMethod doIntraProcedureAnalysis(SootMethod method) {
        Log.info("do intra procedure analysis on %s", method.getSignature());
        if (!method.isConcrete()) {
            return null;
        }

        Body body = method.retrieveActiveBody();
        if (!patchedClasses.contains(method.getDeclaringClass())) {
            PatchJimple patchJimple = new PatchJimple(hierarchy, method.getDeclaringClass());
            patchJimple.patch();
        }

        IntraAnalyzedMethod analyzedMethod = new IntraAnalyzedMethod(method);
        int order = 1;
        for (Unit unit : body.getUnits()) {
            unit.addTag(new LocationTag(method, order, unit));
            StmtVisitor stmtVisitor = StmtVisitor.getInstance(analyzedMethod, unit);
            unit.apply(stmtVisitor);
            order++;
        }
        return analyzedMethod;
    }

    public Map<String, IntraAnalyzedMethod> extractPointRelation() {
        Map<String, IntraAnalyzedMethod> intraAnalyzedMethods = new HashMap<>();
        Set<String> visitedMethods = new HashSet<>();
        Scene.v().getApplicationClasses().snapshotIterator().forEachRemaining(sootClass -> sootClass.getMethods().forEach(new Consumer<SootMethod>() {
            @Override
            public void accept(SootMethod sootMethod) {
                if (needDoIntraAnalysis(sootMethod)) {
                    addAnalyzedMethod(doIntraProcedureAnalysis(sootMethod));
                }
                visitedMethods.add(sootMethod.getSignature());
            }

            private boolean needDoIntraAnalysis(SootMethod sootMethod) {
                return !visitedMethods.contains(sootMethod.getSignature()) && sootMethod.getDeclaringClass().isApplicationClass();
            }

            private void addAnalyzedMethod(IntraAnalyzedMethod analyzedMethod) {
                if (analyzedMethod == null) {
                    return;
                }
                intraAnalyzedMethods.put(analyzedMethod.getSignature(), analyzedMethod);
            }
        }));
        return intraAnalyzedMethods;
    }
}
