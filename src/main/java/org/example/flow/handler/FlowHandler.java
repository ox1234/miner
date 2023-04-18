package org.example.flow.handler;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.*;
import org.example.flow.context.ContextMethod;

public interface FlowHandler<T> {
    void preHandle(Node to, AbstractExprNode from);

    void postHandle(Node to, AbstractExprNode from);

    void handle(IntraAnalyzedMethod.AnalyzedUnit analyzedUnit);

    void doAnalysis(ContextMethod entry);

    boolean preTransferLeft(Node to, AbstractExprNode from, T result);

    void transferLeft(Node to, T from);

    T handleEmptyExprNode(EmptyExprNode from);

    T handleOpExprNode(OpExprNode from);

    T handleMultiExprNode(MultiExprNode from);

    T handleSingleExprNode(SingleExprNode from);

    void preProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod);

    void postProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod);

    void preProcessMethod(ContextMethod currentMethod);

    void postProcessMethod(ContextMethod currentMethod);
}
