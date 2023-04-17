package org.example.flow.handler;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.*;
import org.example.flow.*;
import org.example.flow.context.ContextMethod;

import java.lang.reflect.Array;
import java.util.*;

abstract public class AbstractFlowHandler<T> implements FlowHandler<T> {
    protected FlowEngine flowEngine;
    protected CallStack callStack;
    protected List<Collector> collectors;
    protected IntraAnalyzedMethod.AnalyzedUnit currentUnit;


    protected AbstractFlowHandler(FlowEngine flowEngine, Collector... collectors) {
        this.flowEngine = flowEngine;
        this.callStack = flowEngine.getCallStack();
        this.collectors = Arrays.asList(collectors);
    }

    public TaintContainer getTaintContainer() {
        return callStack.peek().getTaintContainer();
    }

    public PointToContainer getPointContainer() {
        return callStack.peek().getPointToContainer();
    }

    @Override
    public void doAnalysis(ContextMethod entry) {
        callStack.push(entry);
        preProcessMethod(entry);
        IntraAnalyzedMethod analyzedMethod = entry.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            analyzedMethod.getOrderedFlowMap().forEach((node, analyzedUnit) -> handle(analyzedUnit));
        }
        postProcessMethod(entry);

        doCollect();
        callStack.pop();
    }

    @Override
    public void preHandle(Node to, AbstractExprNode from) {

    }

    @Override
    public void postHandle(Node to, AbstractExprNode from) {

    }

    @Override
    public void handle(IntraAnalyzedMethod.AnalyzedUnit analyzedUnit) {
        Node to = analyzedUnit.getTo();
        AbstractExprNode from = analyzedUnit.getFrom();

        IntraAnalyzedMethod.AnalyzedUnit oldCurrentUnit = null;
        if (currentUnit != null) {
            oldCurrentUnit = currentUnit;
        }
        currentUnit = analyzedUnit;
        preHandle(to, from);

        T rightRes = null;
        if (from instanceof EmptyExprNode) {
            rightRes = handleEmptyExprNode((EmptyExprNode) from);
        } else if (from instanceof OpExprNode) {
            rightRes = handleOpExprNode((OpExprNode) from);
        } else if (from instanceof MultiExprNode) {
            rightRes = handleMultiExprNode((MultiExprNode) from);
        } else if (from instanceof SingleExprNode) {
            rightRes = handleSingleExprNode((SingleExprNode) from);
        }
        if (preTransferLeft(to, rightRes)) {
            transferLeft(to, rightRes);
        }
        postHandle(to, from);

        currentUnit = oldCurrentUnit;
    }

    @Override
    public boolean preTransferLeft(Node to, T from) {
        return true;
    }

    protected Set<ContextMethod> handleCallNode(CallNode callNode) {
        CtxCG ctxCG = flowEngine.getCtxCG();
        Set<ContextMethod> callees = ctxCG.getCallNodeDispatchMethods(callNode);
        for (ContextMethod callee : callees) {
            if (callStack.contains(callee)) {
                continue;
            }
            preProcessCallNode(callNode, callee);
            doAnalysis(callee);
            postProcessCallNode(callNode, callee);
        }

        return new LinkedHashSet<>(callees);
    }

    private void doCollect() {
        for (Collector collector : collectors) {
            collector.collect(callStack);
        }
    }

    @Override
    public void preProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod) {
    }

    @Override
    public void postProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod) {
    }

    @Override
    public void preProcessMethod(ContextMethod currentMethod) {

    }

    @Override
    public void postProcessMethod(ContextMethod currentMethod) {

    }

    // ---------------------------------------------- helper methods -----------------------------------------------------------

}
