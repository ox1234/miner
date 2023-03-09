package org.example.flow.handler.impl;

import org.example.core.basic.Node;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.AbstractExprNode;
import org.example.flow.FlowEngine;
import org.example.flow.handler.SingleExprHandler;

public class NodeParameterHandler extends SingleExprHandler {
    public NodeParameterHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public boolean canHandle(Node to, AbstractExprNode from) {
        // handle: a = param-0;
        return super.canHandle(to, from) && super.getNode(from) instanceof Parameter;
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        if (super.getTaintContainer().containsTaint(super.getNode(from))) {
            super.getTaintContainer().addTaint(to);
        }
    }
}
