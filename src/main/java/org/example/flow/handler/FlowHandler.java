package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;

public interface FlowHandler {
    boolean canHandle(Node to, AbstractExprNode from);

    void handle(Node to, AbstractExprNode from);
}
