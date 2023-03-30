package org.example.flow.handler.impl;

import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.ObjField;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.flow.FlowEngine;
import org.example.flow.PointToContainer;
import org.example.flow.handler.AbstractFlowHandler;

abstract public class BasicFlowHandler extends AbstractFlowHandler {
    protected BasicFlowHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        handlePointRelation(to, from);
    }

    protected void handlePointRelation(Node to, AbstractExprNode from) {
        // from is not single expr node, will skip
        if (!(from instanceof SingleExprNode)) {
            return;
        }

        Node rightNode = from.getFirstNode();

        // if right node is call node, do param mapping
        if (rightNode instanceof CallNode) {
            CallNode callNode = (CallNode) rightNode;
            for (int i = 0; i < callNode.getArgs().size(); i++) {
                Node arg = callNode.getArgs().get(i);
                Node param = callNode.getParams().get(i);
                handleLeftAndRight(param, arg);
            }
        } else {
            handleLeftAndRight(to, rightNode);
        }
    }

    protected void handleLeftAndRight(Node to, Node from) {
        Obj refObj = getNodeRefObj(from);
        if (refObj == null) {
            return;
        }
        propagatePoint(to, refObj);
    }

    protected void propagatePoint(Node to, Obj obj) {
        if (to instanceof LocalVariable) {
            getPointContainer().addPointRelation(to, obj);
        } else if (to instanceof InstanceField) {
            InstanceField field = (InstanceField) to;
            Obj baseObj = callStack.getBaseRefObj(field.getBase());
            baseObj.putInstanceField(field, obj);
        } else if (to instanceof Parameter) {
            getPointContainer().addPointRelation(to, obj);
        }
    }

    protected Obj getNodeRefObj(Node rightNode) {
        if (rightNode instanceof Obj) {
            return (Obj) rightNode;
        } else if (rightNode instanceof LocalVariable) {
            return getPointContainer().getPointRefObj(rightNode);
        } else if (rightNode instanceof InstanceField) {
            InstanceField field = (InstanceField) rightNode;
            Obj baseObj = callStack.getBaseRefObj(field.getBase());
            ObjField refField = baseObj.getInstanceField(field);
            Node val = refField.getValueNode();
            getNodeRefObj(val);
        } else if (rightNode instanceof Parameter) {
            return getPointContainer().getPointRefObj(rightNode);
        }
        return null;
    }
}
