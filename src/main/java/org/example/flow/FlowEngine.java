package org.example.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.entry.IEntry;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.collector.debug.TaintVarCollector;
import org.example.flow.collector.vuln.VulnCollector;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.FlowHandler;
import org.example.flow.handler.impl.PointFlowHandler;
import org.example.flow.handler.impl.SanitizedTaintFlowHandler;
import org.example.util.MethodUtil;
import soot.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        registerFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER, new SanitizedTaintFlowHandler(this, Collections.emptySet(), Collections.emptySet(), new VulnCollector(), new TaintVarCollector()));
    }

    public void registerFlowHandler(FlowHandlerEnum flowType, FlowHandler<?> flowHandler) {
        flowHandlers.put(flowType, flowHandler);
    }

    public FlowHandler<?> getFlowHandler(FlowHandlerEnum flowType) {
        return flowHandlers.get(flowType);
    }

    public void doAnalysis(IEntry iEntry) {
        SootMethod entryPoint = iEntry.entryMethod();
        SootClass entryClass = entryPoint.getDeclaringClass();

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
        if (iEntry.isParamTaint()) {
            entry.taintAllParams();
        }

        for (FlowHandlerEnum value : FlowHandlerEnum.values()) {
            logger.info(String.format("do %s analysis from %s entry", value, entryPoint.getSignature()));
            Instant startTime = Instant.now();
            getFlowHandler(value).doAnalysis(entry);
            logger.info(String.format("%s analysis finished from %s entry cost %ds", value, entryPoint.getSignature(), Duration.between(startTime, Instant.now()).getSeconds()));
        }
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
