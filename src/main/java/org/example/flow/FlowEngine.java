package org.example.flow;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.core.expr.AbstractExprNode;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.flow.handler.impl.PointFlowHandler;
import org.example.flow.handler.impl.TaintFlowHandler;
import org.example.soot.SootHelper;
import org.example.util.Log;
import org.example.util.MethodUtil;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import soot.*;

import java.util.*;
import java.util.function.BiConsumer;

public class FlowEngine {
    private Map<FlowHandlerEnum, AbstractFlowHandler> flowHandlers;

    public static Map<String, IntraAnalyzedMethod> intraAnalyzedMethodMap;
    private Graph<SootMethod, CallNode> cg = new DefaultDirectedGraph<>(CallNode.class);

    private CallStack callStack;
    private Hierarchy hierarchy;

    public FlowEngine(Map<String, IntraAnalyzedMethod> analyzedMethodMap) {
        intraAnalyzedMethodMap = analyzedMethodMap;

        this.callStack = new CallStack();
        this.hierarchy = Scene.v().getActiveHierarchy();

        this.flowHandlers = new HashMap<>();
        registerFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER, new PointFlowHandler(this));
        registerFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER, new TaintFlowHandler(this));
    }

    public void registerFlowHandler(FlowHandlerEnum flowType, AbstractFlowHandler flowHandler) {
        flowHandlers.put(flowType, flowHandler);
    }

    public AbstractFlowHandler getFlowHandler(FlowHandlerEnum flowType) {
        return flowHandlers.get(flowType);
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
        entry.genFakeParamObj();

        traverse(entry);
    }

    public void traverse(ContextMethod entry) {
        callStack.push(entry);
        IntraAnalyzedMethod analyzedMethod = entry.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            // if taint all param, all parameter of this method will be taint
            if (entry.isTaintAllParam()) {
                analyzedMethod.getParameterNodes().forEach(parameter -> entry.getTaintContainer().addTaint(parameter));
            }

            // do point analysis
            analyzedMethod.getOrderedFlowMap().forEach((node, abstractExprNode) -> getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER).handle(node, abstractExprNode));
            // do taint analysis
            analyzedMethod.getOrderedFlowMap().forEach((node, abstractExprNode) -> {
                TaintContainer taintContainer = entry.getTaintContainer();
                Log.debug("%s method with stmt %s such taint: %s", entry.getSootMethod().getSignature(), node.getRefStmt(), taintContainer);
                getFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER).handle(node, abstractExprNode);
            });
        }
        callStack.pop();
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
