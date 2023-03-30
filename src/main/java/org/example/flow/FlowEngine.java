package org.example.flow;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.flow.handler.impl.TaintFlowHandler;
import org.example.soot.SootHelper;
import org.example.util.Log;
import org.example.util.MethodUtil;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import soot.*;

import java.util.*;

public class FlowEngine {
    private List<AbstractFlowHandler> flowHandlers;

    public static Map<String, IntraAnalyzedMethod> intraAnalyzedMethodMap;
    private Graph<SootMethod, CallNode> cg = new DefaultDirectedGraph<>(CallNode.class);

    private CallStack callStack;
    private Hierarchy hierarchy;

    public FlowEngine(Map<String, IntraAnalyzedMethod> analyzedMethodMap) {
        intraAnalyzedMethodMap = analyzedMethodMap;

        this.callStack = new CallStack();
        this.hierarchy = Scene.v().getActiveHierarchy();

        this.flowHandlers = new ArrayList<>();
        registerFlowHandler(new TaintFlowHandler(this));
    }

    public void registerFlowHandler(AbstractFlowHandler flowHandler) {
        flowHandlers.add(flowHandler);
    }

    public void traverse(SootMethod entryPoint) {
        // entry point need traverse init and clinit method first
        SootClass entryClass = entryPoint.getDeclaringClass();
        Obj fakeObj = new PhantomObj(entryClass);

        SootMethod clinit = MethodUtil.getRefInitMethod(entryClass, true);
        SootMethod init = MethodUtil.getRefInitMethod(entryClass, false);
        if (clinit != null) {
            ContextMethod ctxClinit = new StaticContextMethod(entryClass, clinit, null, null);
            traverse(ctxClinit);
        }

        if (!entryPoint.isStatic() && init != null) {
            ContextMethod ctxInit = new InstanceContextMethod(fakeObj, init, null, null);
            traverse(ctxInit);
        }


        // do entry point traverse
        ContextMethod entry = new InstanceContextMethod(fakeObj, entryPoint, null, null);
        entry.setTaintAllParam(true);
        traverse(entry);
    }

    public boolean traverse(ContextMethod entry) {
        callStack.push(entry);
        IntraAnalyzedMethod analyzedMethod = entry.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            // if taint all param, all parameter of this method will be taint
            if (entry.isTaintAllParam()) {
                analyzedMethod.getParameterNodes().forEach(parameter -> entry.getTaintContainer().addTaint(parameter));
            }

            // add method has relation
            analyzedMethod.getOrderedFlowMap().forEach((node, abstractExprNode) -> {
                for (AbstractFlowHandler flowHandler : flowHandlers) {
                    Log.debug("method: %s, stmt: %s, now taint status: %s", entry.getSootMethod().getSignature(), node.getRefStmt(), flowHandler.getTaintContainer());
                    flowHandler.handle(node, abstractExprNode);
                    Log.debug("method: %s, stmt: %s, now taint status: %s", entry.getSootMethod().getSignature(), node.getRefStmt(), flowHandler.getTaintContainer());
                }
            });
        }
        callStack.pop();
        return entry.getTaintContainer().isRetTaint();
    }

    public void addCGEdge(CallNode callNode) {
        SootMethod srcMethod = callNode.getCaller();
        SootMethod tgtMethod = callNode.getCallee();
        cg.addVertex(srcMethod);
        cg.addVertex(tgtMethod);
        cg.addEdge(srcMethod, tgtMethod, callNode);
    }


    public CallStack getCallStack() {
        return callStack;
    }

    public Hierarchy getHierarchy() {
        return hierarchy;
    }

    public static IntraAnalyzedMethod getAnalysedMethod(SootMethod sootMethod) {
        return getAnalysedMethod(sootMethod.getSignature());
    }

    public static IntraAnalyzedMethod getAnalysedMethod(String signature) {
        IntraAnalyzedMethod intraAnalyzedMethod = intraAnalyzedMethodMap.get(signature);
        if (intraAnalyzedMethod == null) {
            SootMethod sootMethod = SootHelper.getSootMethod(signature);
            if (sootMethod != null) {
                intraAnalyzedMethod = new IntraAnalyzedMethod(sootMethod);
            }
        }
        return intraAnalyzedMethod;
    }

    public static void addAnalysedMethod(IntraAnalyzedMethod intraAnalyzedMethod) {
        intraAnalyzedMethodMap.put(intraAnalyzedMethod.getSignature(), intraAnalyzedMethod);
    }
}
