package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;

abstract public class SingleExprHandler extends AbstractFlowHandler {
    public SingleExprHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public boolean canHandle(Node to, AbstractExprNode from) {
        return from instanceof SingleExprNode;
    }

    public Node getNode(AbstractExprNode from) {
        return from.getFirstNode();
    }
}
