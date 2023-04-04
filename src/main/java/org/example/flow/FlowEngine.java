package org.example.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.FlowHandler;
import org.example.flow.handler.impl.PointFlowHandler;
import org.example.flow.handler.impl.TaintFlowHandler;
import org.example.util.MethodUtil;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;

public class FlowEngine {
    private final Logger logger = LogManager.getLogger(FlowEngine.class);
    private Map<FlowHandlerEnum, FlowHandler<?>> flowHandlers;
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


    public void registerFlowHandler(FlowHandlerEnum flowType, FlowHandler<?> flowHandler) {
        flowHandlers.put(flowType, flowHandler);
    }

    public FlowHandler<?> getFlowHandler(FlowHandlerEnum flowType) {
        return flowHandlers.get(flowType);
    }

    public void doAnalysis(SootMethod entryPoint) {
        SootClass entryClass = entryPoint.getDeclaringClass();

        // no matter what, clinit method will always be called
        SootMethod clinit = MethodUtil.getRefInitMethod(entryClass, true);
        if (clinit != null) {
            ContextMethod ctxClinit = new StaticContextMethod(entryClass, clinit, null, null);
            logger.info(String.format("do %s class clinit method's point analysis", entryPoint.getSignature()));
            doAnalysis(ctxClinit, getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER));
        }

        ContextMethod entry;
        if (entryPoint.isStatic()) {
            entry = new StaticContextMethod(entryClass, entryPoint, null, null);
        } else {
            // entry point need doAnalysis init and clinit method first
            Obj fakeObj = new PhantomObj(entryClass, entryPoint.getActiveBody().getThisUnit());
            SootMethod init = MethodUtil.getRefInitMethod(entryClass, false);
            if (init != null) {
                ContextMethod ctxInit = new InstanceContextMethod(fakeObj, init, null, null);
                logger.info(String.format("do %s class init method's point analysis", entryPoint.getSignature()));
                doAnalysis(ctxInit, getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER));
            }
            entry = new InstanceContextMethod(fakeObj, entryPoint, null, null);
        }

        // generate input param object
        entry.genFakeParamObj();

        // do point analysis, and build call graph
        logger.info(String.format("do point analysis and build call graph from %s entry", entryPoint.getSignature()));
        doAnalysis(entry, getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER));

        // do taint analysis
        logger.info(String.format("do taint analysis from %s entry", entryPoint.getSignature()));
        TaintFlowHandler taintFlowHandler = (TaintFlowHandler) getFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER);

        taintFlowHandler.taintMethodParam(entry);
        doAnalysis(entry, taintFlowHandler);
        logger.info(String.format("flow analysis is finished from %s entry", entryPoint.getSignature()));
    }

    public void doAnalysis(ContextMethod entry, FlowHandler<?> flowHandler) {
        callStack.push(entry);
        flowHandler.preProcessMethod(entry);
        IntraAnalyzedMethod analyzedMethod = entry.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            analyzedMethod.getOrderedFlowMap().forEach(flowHandler::handle);
        }
        flowHandler.postProcessMethod(entry);
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
            SootMethod sootMethod = MethodUtil.getSootMethod(signature);
            if (sootMethod != null) {
                intraAnalyzedMethod = IntraAnalyzedMethod.getInstance(sootMethod);
            }
        }
        return intraAnalyzedMethod;
    }
}
