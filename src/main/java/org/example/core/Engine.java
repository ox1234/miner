package org.example.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Configuration;
import org.example.core.hook.builtin.OutputHook;
import org.example.core.hook.bytedance.CodeGraphHook;
import org.example.core.hook.bytedance.EngineHook;
import org.example.core.visitor.StmtVisitor;
import soot.*;
import soot.Unit;

import java.util.*;
import java.util.function.Consumer;

public class Engine {
    private final Logger logger = LogManager.getLogger(Engine.class);
    private Hierarchy hierarchy;
    private Set<SootClass> patchedClasses;
    private List<EngineHook> engineHooks;

    public Engine(Hierarchy hierarchy) {
        this.hierarchy = hierarchy;
        this.patchedClasses = new HashSet<>();
        this.engineHooks = new ArrayList<>();

        addEngineHook(new OutputHook());
        addEngineHook(new CodeGraphHook());
    }

    public void addEngineHook(EngineHook hook) {
        engineHooks.add(hook);
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
        Scene.v().getApplicationClasses().snapshotIterator().forEachRemaining(new Consumer<>() {
            @Override
            public void accept(SootClass sootClass) {
                engineHooks.forEach(engineHook -> {
                    if (engineHook.enabled()) {
                        engineHook.hookClass(sootClass);
                    }
                });
                for (SootMethod method : sootClass.getMethods()) {
                    if (visitedMethods.contains(method)) {
                        continue;
                    }
                    visitedMethods.add(method);
                    IntraAnalyzedMethod analyzedMethod = analysis(method);
                    if (analyzedMethod != null) {
                        intraAnalyzedMethods.put(analyzedMethod.getSignature(), analyzedMethod);
                        engineHooks.forEach(engineHook -> {
                            if (engineHook.enabled()) {
                                engineHook.hookMethod(analyzedMethod);
                            }
                        });
                    }
                }
            }

            private IntraAnalyzedMethod analysis(SootMethod sootMethod) {
                return doIntraProcedureAnalysis(sootMethod);
            }
        });
        engineHooks.forEach(engineHook -> {
            if (engineHook.enabled()) {
                engineHook.engineFinish();
            }
        });
        return intraAnalyzedMethods;
    }
}
