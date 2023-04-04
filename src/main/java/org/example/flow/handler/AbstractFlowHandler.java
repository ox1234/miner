package org.example.flow.handler;

import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.core.expr.*;
import org.example.flow.*;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;

abstract public class AbstractFlowHandler<T> implements FlowHandler<T> {
    protected FlowEngine flowEngine;
    protected CallStack callStack;

    protected AbstractFlowHandler(FlowEngine flowEngine) {
        this.flowEngine = flowEngine;
        this.callStack = flowEngine.getCallStack();
    }

    public TaintContainer getTaintContainer() {
        return callStack.peek().getTaintContainer();
    }

    public PointToContainer getPointContainer() {
        return callStack.peek().getPointToContainer();
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
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
        transferLeft(to, rightRes);
    }

    protected Set<ContextMethod> handleCallNode(CallNode callNode) {
        CtxCG ctxCG = flowEngine.getCtxCG();
        Set<ContextMethod> callees = ctxCG.getCallNodeDispatchMethods(callNode);
        for (ContextMethod callee : callees) {
            if (callStack.contains(callee)) {
                continue;
            }
            preProcessCallNode(callNode, callee);
            flowEngine.doAnalysis(callee, this);
            postProcessCallNode(callNode, callee);
        }

        return new LinkedHashSet<>(callees);
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
