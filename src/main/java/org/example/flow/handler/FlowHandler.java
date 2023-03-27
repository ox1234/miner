package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;

public interface FlowHandler {
    void handle(Node to, AbstractExprNode from);
}
