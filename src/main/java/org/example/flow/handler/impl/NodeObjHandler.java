package org.example.flow.handler.impl;

import org.example.flow.CallStack;
import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;
import org.example.core.basic.obj.Obj;
import org.example.flow.FlowEngine;
import org.example.flow.handler.SingleExprHandler;

public class NodeObjHandler extends SingleExprHandler {
    public NodeObjHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public boolean canHandle(Node to, AbstractExprNode from) {
        // handle: a = new Obj();
        return super.canHandle(to, from) && super.getNode(from) instanceof Obj;
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        Node fromNode = super.getNode(from);
        to.setRefObj((Obj) fromNode);
    }
}
