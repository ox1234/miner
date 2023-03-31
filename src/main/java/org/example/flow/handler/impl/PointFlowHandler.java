package org.example.flow.handler.impl;

import fj.data.fingertrees.Single;
import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.ObjField;
import org.example.core.expr.*;
import org.example.flow.FlowEngine;
import org.example.flow.context.ContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import soot.SootMethod;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PointFlowHandler extends AbstractFlowHandler {
    public PointFlowHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        propagatePoint(to, handleRight(from));
    }

    private void propagatePoint(Node to, Set<Obj> objs) {
        if (to instanceof LocalVariable) {
            getPointContainer().addPointRelation(to, objs);
        } else if (to instanceof InstanceField) {
            InstanceField field = (InstanceField) to;
            Set<Obj> baseObjs = callStack.getBaseRefObj(field.getBase());
            for (Obj obj : objs) {
                for (Obj baseObj : baseObjs) {
                    baseObj.putInstanceField(field, obj);
                }
            }
        } else if (to instanceof Parameter) {
            getPointContainer().addPointRelation(to, objs);
        } else if (to instanceof UnifyReturn) {
            getPointContainer().addPointRelation(to, objs);
        }
    }

    public Set<Obj> handleRight(AbstractExprNode from) {
        if (from instanceof SingleExprNode) {
            return handleRight((SingleExprNode) from);
        } else if (from instanceof MultiExprNode) {
            return handleRight((MultiExprNode) from);
        }
        return Collections.emptySet();
    }

    protected Set<Obj> handleRight(SingleExprNode from) {
        Node rightNode = from.getFirstNode();
        return handleNode(rightNode);
    }

    protected Set<Obj> handleRight(MultiExprNode from) {
        Set<Obj> refObjs = new LinkedHashSet<>();
        for (Node node : from.getAllNodes()) {
            refObjs.addAll(getNodeRefObj(node));
        }
        return refObjs;
    }

    private Set<Obj> handleNode(Node node) {
        Set<Obj> objs = new LinkedHashSet<>();
        // if right node is call node, do param mapping
        if (node instanceof CallNode) {
            objs.addAll(handleCallNode((CallNode) node));
        } else {
            objs.addAll(getNodeRefObj(node));
        }
        return objs;
    }

    private Set<Obj> handleCallNode(CallNode callNode) {
        Set<Obj> retObjs = new LinkedHashSet<>();
        for (SootMethod tgtMethod : dispatch(callNode)) {
            for (ContextMethod tgtContextMethod : getTargetContextMethod(callNode, tgtMethod)) {
                // map param - arg point relation
                for (int i = 0; i < callNode.getArgs().size(); i++) {
                    Node argNode = callNode.getArgs().get(i);
                    Node paramNode = tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i);
                    tgtContextMethod.getPointToContainer().addLocalPointRelation(paramNode, getNodeRefObj(argNode));
                }

                if (tgtContextMethod != null && !callStack.contains(tgtContextMethod)) {
                    flowEngine.traverse(tgtContextMethod);
                }
            }
        }
        return retObjs;
    }

    private Set<Obj> getNodeRefObj(Node rightNode) {
        Set<Obj> objs = new LinkedHashSet<>();
        if (rightNode instanceof Obj) {
            objs.add((Obj) rightNode);
        } else if (rightNode instanceof LocalVariable) {
            objs.addAll(getPointContainer().getPointRefObj(rightNode));
        } else if (rightNode instanceof InstanceField) {
            InstanceField field = (InstanceField) rightNode;
            Set<Obj> baseObjs = callStack.getBaseRefObj(field.getBase());
            Set<Obj> refObjs = new LinkedHashSet<>();
            for (Obj baseObj : baseObjs) {
                ObjField refField = baseObj.getInstanceField(field);
                Node val = refField.getValueNode();
                refObjs.addAll(getNodeRefObj(val));
            }
            objs.addAll(refObjs);
        } else if (rightNode instanceof Parameter) {
            objs.addAll(getPointContainer().getPointRefObj(rightNode));
        }
        return objs;
    }
}
