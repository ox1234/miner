package org.example.flow.handler.impl;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.expr.*;
import org.example.flow.FlowEngine;
import org.example.flow.context.ContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.util.Log;

import java.util.*;

public class TaintFlowHandler extends AbstractFlowHandler<Boolean> {
    public TaintFlowHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    public void taintMethodParam(ContextMethod contextMethod) {
        IntraAnalyzedMethod analyzedMethod = contextMethod.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            analyzedMethod.getParameterNodes().forEach(parameter -> contextMethod.getTaintContainer().addTaint(parameter));
        }
    }

    @Override
    public void transferLeft(Node to, Boolean from) {
        if (!from) {
            return;
        }
        addLeftTaint(to);
    }

    @Override
    public Boolean handleEmptyExprNode(EmptyExprNode from) {
        return false;
    }

    @Override
    public Boolean handleOpExprNode(OpExprNode from) {
        for (Node node : from.getAllNodes()) {
            if (isRightTaint(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean handleMultiExprNode(MultiExprNode from) {
        for (Node node : from.getAllNodes()) {
            if (isRightTaint(node)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean handleSingleExprNode(SingleExprNode from) {
        return isRightTaint(from.getFirstNode());
    }

    @Override
    public void preProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod) {
        // if base node is taint, pass it to target method
        if (getTaintContainer().containsTaint(callNode.getBase())) {
            tgtContextMethod.getTaintContainer().addTaint(callNode.getBase());
        }

        // do arg param taint map
        List<Node> args = callNode.getArgs();
        if (args != null && args.size() > 0) {
            for (int i = 0; i < args.size(); i++) {
                // map taint variable
                if (getTaintContainer().containsTaint(args.get(i))) {
                    tgtContextMethod.getTaintContainer().addTaint(tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i));
                }
                // map point variable
                Set<Obj> obj = getPointContainer().getPointRefObj(args.get(i));
                if (obj != null) {
                    tgtContextMethod.getPointToContainer().addPointRelation(tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i), obj);
                }
            }
        }

        // check if reach sink, if reach will report vulnerability
        if (tgtContextMethod.checkReachSink()) {
            Log.info("!!! find vulnerability reach sink to %s", tgtContextMethod.getSootMethod().getSignature());
        }

        // if target method is abstract, and its parameter is taint, the method's return will be taint
        if (tgtContextMethod.getSootMethod().isAbstract() && tgtContextMethod.getTaintContainer().isParamTaint()) {
            tgtContextMethod.setReturnTaint(true);
        }
    }

    private void addLeftTaint(Node node) {
        if (node instanceof LocalVariable) {
            getTaintContainer().addTaint(node);
        } else if (node instanceof InstanceField) {
            InstanceField field = (InstanceField) node;
            Set<Obj> baseObjs = callStack.getBaseRefObj(field.getBase());
            for (Obj baseObj : baseObjs) {
                baseObj.setTaintField(field);
            }
        } else if (node instanceof StaticField) {
            getTaintContainer().addTaint(node);
        } else if (node instanceof UnifyReturn) {
            callStack.peek().setReturnTaint(true);
        }
    }

    private Boolean isRightTaint(Node node) {
        if (node instanceof CallNode) {
            Set<ContextMethod> contextMethods = super.handleCallNode((CallNode) node);
            for (ContextMethod contextMethod : contextMethods) {
                if (contextMethod.returnTaint()) {
                    return true;
                }
            }
            return false;
        } else if (node instanceof Parameter) {
            return getTaintContainer().containsTaint(node);
        } else if (node instanceof LocalVariable) {
            return getTaintContainer().containsTaint(node);
        } else if (node instanceof UnifyReturn) {
            return getTaintContainer().containsTaint(node);
        } else if (node instanceof StaticField) {
            return getTaintContainer().containsTaint(node);
        } else if (node instanceof InstanceField) {
            InstanceField field = (InstanceField) node;
            Node base = field.getBase();
            Set<Obj> baseObj = callStack.getBaseRefObj(base);
            for (Obj obj : baseObj) {
                if (obj.isTaintField(field)) {
                    return true;
                }
            }
        }
        return false;
    }
}
