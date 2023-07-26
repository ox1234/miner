package org.example.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.basic.node.CallNode;
import org.example.flow.collector.debug.TaintVarCollector;
import org.example.flow.context.ContextMethod;

import java.util.*;

public class CtxCG {
    private final Logger logger = LogManager.getLogger(CtxCG.class);
    Map<ContextMethod, List<ContextMethod>> ctxCG = new LinkedHashMap<>();
    Map<CallNode, Set<ContextMethod>> dispatchMap = new LinkedHashMap<>();

    public void addCtxCGEdge(ContextMethod src, ContextMethod tgt) {
        logger.debug(String.format("add call graph edge %s -> %s", src.getSootMethod().getSignature(), tgt.getSootMethod().getSignature()));
        if (!ctxCG.containsKey(src)) {
            ctxCG.put(src, new ArrayList<>());
        }
        ctxCG.get(src).add(tgt);
    }

    public void addCallNodeDispatch(CallNode callNode, ContextMethod tgt) {
        if (!dispatchMap.containsKey(callNode)) {
            dispatchMap.put(callNode, new LinkedHashSet<>());
        }
        dispatchMap.get(callNode).add(tgt);
    }

    public List<ContextMethod> getCallee(ContextMethod caller) {
        return ctxCG.get(caller);
    }

    public Set<ContextMethod> getCallNodeDispatchMethods(CallNode callNode) {
        Set<ContextMethod> dispatchedMethods = dispatchMap.get(callNode);
        if (dispatchedMethods == null) {
            dispatchedMethods = new LinkedHashSet<>();
        }
        return dispatchedMethods;
    }
}
