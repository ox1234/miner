package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.OpExprNode;
import org.example.flow.FlowEngine;

abstract public class OpExprHandler extends AbstractFlowHandler {
    protected OpExprHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public boolean canHandle(Node to, AbstractExprNode from) {
        return from instanceof OpExprNode;
    }
}
