package org.example.flow.handler.impl;

import org.example.constant.Operation;
import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.OpExprNode;
import org.example.flow.FlowEngine;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.flow.handler.OpExprHandler;

import java.util.List;

public class NodeStrConcatHandler extends OpExprHandler {
    protected NodeStrConcatHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public boolean canHandle(Node to, AbstractExprNode from) {
        // handle: a = "/bin/bash" + cmd
        return from instanceof OpExprNode && ((OpExprNode) from).getOp() == Operation.STRCONCAT;
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        for (Node fromNode : from.getAllNodes()) {
            if (getTaintContainer().containsTaint(fromNode)) {
                getTaintContainer().addTaint(to);
                return;
            }
        }
    }
}
