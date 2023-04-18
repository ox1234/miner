package org.example.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.Loc;
import org.example.core.RouteIntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.collector.vuln.VulnCollector;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.FlowHandler;
import org.example.flow.handler.impl.PointFlowHandler;
import org.example.flow.handler.impl.SanitizedTaintFlowHandler;
import org.example.flow.handler.impl.TaintFlowHandler;
import org.example.util.MethodUtil;
import soot.*;

import java.util.*;
import java.util.function.BiConsumer;

public class FlowEngine {
    private final Logger logger = LogManager.getLogger(FlowEngine.class);
    private Map<FlowHandlerEnum, FlowHandler<?>> flowHandlers;
    public static Map<String, IntraAnalyzedMethod> intraAnalyzedMethodMap;

    private CtxCG ctxCG;
    private CallStack callStack;

    public FlowEngine(Map<String, IntraAnalyzedMethod> analyzedMethodMap) {
        intraAnalyzedMethodMap = analyzedMethodMap;

        this.ctxCG = new CtxCG();
        this.callStack = new CallStack();
        this.flowHandlers = new HashMap<>();

        registerFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER, new PointFlowHandler(this));
        registerFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER, new SanitizedTaintFlowHandler(this, Collections.emptySet(), Collections.emptySet(), new VulnCollector()));
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
            getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER).doAnalysis(ctxClinit);
        }

        ContextMethod entry;
        if (entryPoint.isStatic()) {
            entry = new StaticContextMethod(entryClass, entryPoint, null, null);
        } else {
            Local thisLocal = entryPoint.getActiveBody().getThisLocal();
            Node thisNode = Site.getNodeInstance(ThisVariable.class, entryPoint, thisLocal.getType());
            Obj fakeObj = (Obj) Site.getNodeInstance(PhantomObj.class, entryClass.getType(), thisNode);
            SootMethod init = MethodUtil.getRefInitMethod(entryClass, false);
            if (init != null) {
                ContextMethod ctxInit = new InstanceContextMethod(fakeObj, init, null, null);
                logger.info(String.format("do %s class init method's point analysis", entryPoint.getSignature()));
                getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER).doAnalysis(ctxInit);
            }
            entry = new InstanceContextMethod(fakeObj, entryPoint, null, null);
        }

        // generate input param object
        entry.genFakeParamObj();

        // do point analysis, and build call graph
        logger.info(String.format("do point analysis and build call graph from %s entry", entryPoint.getSignature()));
        getFlowHandler(FlowHandlerEnum.POINT_FLOW_HANDLER).doAnalysis(entry);

        // do taint analysis
        logger.info(String.format("do taint analysis from %s entry", entryPoint.getSignature()));
        TaintFlowHandler taintFlowHandler = (TaintFlowHandler) getFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER);

        entry.taintAllParams();
        taintFlowHandler.doAnalysis(entry);
        logger.info(String.format("flow analysis is finished from %s entry", entryPoint.getSignature()));
    }

    public CtxCG getCtxCG() {
        return ctxCG;
    }

    public CallStack getCallStack() {
        return callStack;
    }

    public Hierarchy getHierarchy() {
        return Scene.v().getActiveHierarchy();
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
