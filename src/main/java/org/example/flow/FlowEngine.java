package org.example.flow;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
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
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;
import java.util.function.BiConsumer;

public class FlowEngine {
    ContextMethod entryCtxMethod;

    private Map<FlowHandlerEnum, AbstractFlowHandler> flowHandlers;

    public static Map<String, IntraAnalyzedMethod> intraAnalyzedMethodMap;


    private CtxCG ctxCG;
    private CallStack callStack;
    private Hierarchy hierarchy;

    public FlowEngine(Map<String, IntraAnalyzedMethod> analyzedMethodMap) {
        intraAnalyzedMethodMap = analyzedMethodMap;

        this.ctxCG = new CtxCG();
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

    public void buildCG(SootMethod entryPoint) {
        // entry point need buildCG init and clinit method first
        SootClass entryClass = entryPoint.getDeclaringClass();
        Obj fakeObj = new PhantomObj(entryClass);

        SootMethod clinit = MethodUtil.getRefInitMethod(entryClass, true);
        SootMethod init = MethodUtil.getRefInitMethod(entryClass, false);
        if (clinit != null) {
            ContextMethod ctxClinit = new StaticContextMethod(entryClass, clinit, null, null);
            buildCG(ctxClinit);
        }

        if (!entryPoint.isStatic() && init != null) {
            ContextMethod ctxInit = new InstanceContextMethod(fakeObj, init, null, null);
            buildCG(ctxInit);
        }

        // do entry point buildCG
        ContextMethod entry = new InstanceContextMethod(fakeObj, entryPoint, null, null);
        entryCtxMethod = entry;
        entry.setTaintAllParam(true);
        entry.genFakeParamObj();

        buildCG(entry);
        doAnalysis(entry);
    }

    public void buildCG(ContextMethod entry) {
        callStack.push(entry);
        IntraAnalyzedMethod analyzedMethod = entry.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            // use point flow to build ctx cg
            analyzedMethod.getOrderedFlowMap().forEach((node, exprNode) ->
            {
                Log.debug("now method %s, stmt: %s, has %d point relation", analyzedMethod.getSignature(), node.getRefStmt(), entry.getPointToContainer().getPointContainerSize());
                getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER).handle(node, exprNode);
                Log.debug("now method %s, stmt: %s,  has %d point relation", analyzedMethod.getSignature(), node.getRefStmt(), entry.getPointToContainer().getPointContainerSize());
            });
        }
        callStack.pop();
    }

    public void doAnalysis(ContextMethod entry) {
        callStack.push(entry);
        IntraAnalyzedMethod analyzedMethod = entry.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            analyzedMethod.getOrderedFlowMap().forEach((node, exprNode) -> {
                if (entry.isTaintAllParam()) {
                    analyzedMethod.getParameterNodes().forEach(parameter -> entry.getTaintContainer().addTaint(parameter));
                }
                Log.debug("%s method with stmt %s such taint: %s", entry.getSootMethod().getSignature(), node.getRefStmt(), entry.getTaintContainer());
                getFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER).handle(node, exprNode);
            });
        }
        callStack.pop();
    }

    public CtxCG getCtxCG() {
        return ctxCG;
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
