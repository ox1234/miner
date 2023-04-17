package org.example.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.visitor.StmtVisitor;
import soot.*;
import soot.Unit;

import java.util.*;
import java.util.function.Consumer;

public class Engine {
    private final Logger logger = LogManager.getLogger(Engine.class);
    private Hierarchy hierarchy;
    private Set<SootClass> patchedClasses;

    public Engine(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
        this.patchedClasses = new HashSet<>();
    }

    // doIntraProcedureAnalysis 对给定的SootMethod，进行过程内的指针分析
    public IntraAnalyzedMethod doIntraProcedureAnalysis(SootMethod method) {
        logger.info(String.format("do intra procedure analysis on %s", method.getSignature()));
        if (!method.isConcrete()) {
            logger.debug(String.format("%s method is not concrete, will skip", method.getSignature()));
            return null;
        }

        Body body = method.retrieveActiveBody();
        if (!patchedClasses.contains(method.getDeclaringClass())) {
            PatchJimple patchJimple = new PatchJimple(hierarchy, method.getDeclaringClass());
            patchJimple.patch();
            patchedClasses.add(method.getDeclaringClass());
        }

        IntraAnalyzedMethod analyzedMethod = IntraAnalyzedMethod.getInstance(method);

        int order = 1;
        for (Unit unit : body.getUnits()) {
            StmtVisitor stmtVisitor = StmtVisitor.getInstance(analyzedMethod, order);
            unit.apply(stmtVisitor);
            order++;
        }
        return analyzedMethod;
    }

    public Map<String, IntraAnalyzedMethod> extractPointRelation() {
        Map<String, IntraAnalyzedMethod> intraAnalyzedMethods = new HashMap<>();
        Set<SootMethod> visitedMethods = new HashSet<>();
        Scene.v().getApplicationClasses().snapshotIterator().forEachRemaining(sootClass -> sootClass.getMethods().forEach(new Consumer<SootMethod>() {
            @Override
            public void accept(SootMethod sootMethod) {
                if (visitedMethods.contains(sootMethod)) {
                    logger.debug(String.format("%s method has finish intra analysis, will skip", sootMethod.getSignature()));
                    return;
                }
                addAnalyzedMethod(doIntraProcedureAnalysis(sootMethod));
                visitedMethods.add(sootMethod);
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
