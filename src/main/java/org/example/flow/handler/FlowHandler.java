package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.*;
import org.example.flow.context.ContextMethod;

import java.util.Set;

public interface FlowHandler<T> {
    void handle(Node to, AbstractExprNode from);

    void transferLeft(Node to, T from);

    T handleEmptyExprNode(EmptyExprNode from);

    T handleOpExprNode(OpExprNode from);

    T handleMultiExprNode(MultiExprNode from);

    T handleSingleExprNode(SingleExprNode from);

    void preProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod);

    void postProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod);
}
