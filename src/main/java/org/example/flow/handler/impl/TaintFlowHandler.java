package org.example.flow.handler.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayLoad;
import org.example.core.basic.identity.*;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.expr.*;
import org.example.flow.collector.Collector;
import org.example.flow.FlowEngine;
import org.example.flow.collector.vuln.VulnCollector;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.handler.AbstractFlowHandler;

import java.util.*;

public class TaintFlowHandler extends AbstractFlowHandler<Boolean> {
    private final Logger logger = LogManager.getLogger(TaintFlowHandler.class);

    public TaintFlowHandler(FlowEngine flowEngine, Collector... collectors) {
        super(flowEngine, collectors);
    }

    public void taintMethodParam(ContextMethod contextMethod) {
        IntraAnalyzedMethod analyzedMethod = contextMethod.getIntraAnalyzedMethod();
        if (analyzedMethod != null) {
            logger.info(String.format("set %s method param is all taint", contextMethod.getSootMethod().getSignature()));
            for (int i = 0; i < analyzedMethod.getParameterNodes().size(); i++) {
                Node paramNode = analyzedMethod.getParameterNodes().get(i);
                contextMethod.getTaintContainer().addTaint(paramNode, callStack.getRefObjs(paramNode), null);
            }
        }
    }

    public VulnCollector getVulnCollector() {
        for (Collector collector : getCollectors()) {
            if (collector instanceof VulnCollector) {
                return (VulnCollector) collector;
            }
        }
        return null;
    }


    @Override
    public void transferLeft(Node to, Boolean from) {
        if (!from || to instanceof VoidNode) {
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
    public void preProcessMethod(ContextMethod currentMethod) {
        if (currentMethod.isAllParamTaint()) {
            taintMethodParam(currentMethod);
        }
    }

    @Override
    public void preProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod) {
        Set<Obj> baseObjs = callStack.getRefObjs(callNode.getBase());
        // if base is taint, set context method base taint flag
        if (getTaintContainer().containsTaint(baseObjs)) {
            // if base node is taint, return is taint
            if (!tgtContextMethod.getSootMethod().getDeclaringClass().isApplicationClass()) {
                tgtContextMethod.setReturnTaint(true);
            }
            tgtContextMethod.setBaseTaint(true);
        }

        // do arg param taint map
        List<Node> args = callNode.getArgs();
        if (args != null && !args.isEmpty()) {
            for (int i = 0; i < args.size(); i++) {
                // map taint variable
                Set<Obj> paramObjs = callStack.getRefObjs(args.get(i));
                if (getTaintContainer().containsTaint(paramObjs)) {
                    tgtContextMethod.getTaintContainer().addTaint(tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i), paramObjs, currentUnit.getStmt());
                }
                // map point variable
                Set<Obj> obj = getPointContainer().getPointRefObj(args.get(i));
                if (obj != null) {
                    tgtContextMethod.getPointToContainer().addPointRelation(tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i), obj);
                }
            }
        }

        // if target method is not application class, and its parameter is taint, the method's return will be taint, this will case false positive
        if (!tgtContextMethod.getSootMethod().getDeclaringClass().isApplicationClass() && tgtContextMethod.getTaintContainer().isParamTaint()) {
            tgtContextMethod.setReturnTaint(true);
        }
    }

    private void addLeftTaint(Node node) {
        // if is arr[idx] = taint, will add both index taint object and arr object
        if (node instanceof ArrayLoad) {
            Node baseNode = ((ArrayLoad) node).getBaseNode();
            getTaintContainer().addTaint(baseNode, callStack.getRefObjs(baseNode), currentUnit.getStmt());
        } else if (node instanceof UnifyReturn) {
            callStack.peek().setReturnTaint(true);
        }
        getTaintContainer().addTaint(node, callStack.getRefObjs(node), currentUnit.getStmt());
    }

    private Boolean isRightTaint(Node node) {
        if (node instanceof CallNode) {
            CallNode callNode = (CallNode) node;
            Set<ContextMethod> contextMethods = super.handleCallNode(callNode);
            for (ContextMethod contextMethod : contextMethods) {
                if (contextMethod.returnTaint()) {
                    if (contextMethod instanceof SpecialContextMethod) {
                        // if a special invoke return value is taint, the call base will taint
                        getTaintContainer().addTaint(callNode.getBase(), callStack.getRefObjs(callNode.getBase()), currentUnit.getStmt());
                        if (callNode.getBase() instanceof ThisVariable) {
                            callStack.peek().setReturnTaint(true);
                        }
                    }
                    return true;
                }
            }
        } else {
            return getTaintContainer().containsTaint(callStack.getRefObjs(node));
        }
        return false;
    }
}
