package org.example.flow.handler.impl;

import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.core.expr.*;
import org.example.flow.FlowEngine;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.util.Log;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.*;

public class TaintFlowHandler extends PointFlowHandler {
    public TaintFlowHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        if (isRightTaint(from)) {
            addLeftTaint(to);
        }
    }

    public boolean isRightTaint(AbstractExprNode from) {
        if (from instanceof SingleExprNode) {
            return isRightTaint((SingleExprNode) from);
        } else if (from instanceof OpExprNode) {
            return isRightTaint((OpExprNode) from);
        } else if (from instanceof EmptyExprNode) {
            return isRightTaint((EmptyExprNode) from);
        } else if (from instanceof MultiExprNode) {
            return isRightTaint((MultiExprNode) from);
        }
        Log.warn("%s type from node is not support", from.getClass());
        return false;
    }

    public boolean isRightTaint(SingleExprNode from) {
        Node rightNode = from.getFirstNode();
        return handleRightNode(rightNode);
    }

    public boolean isRightTaint(OpExprNode from) {
        for (Node node : from.getAllNodes()) {
            if (handleRightNode(node)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRightTaint(MultiExprNode from) {
        for (Node allNode : from.getAllNodes()) {
            if (handleRightNode(allNode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRightTaint(EmptyExprNode from) {
        return false;
    }

    public void addLeftTaint(Node node) {
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

    public boolean handleRightNode(Node node) {
        if (node instanceof CallNode) {
            return handleCallNode((CallNode) node);
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

    private boolean handleCallNode(CallNode callNode) {
        boolean retIsTaint = false;

        for (SootMethod tgtMethod : dispatch(callNode)) {
            for (ContextMethod tgtContextMethod : getTargetContextMethod(callNode, tgtMethod)) {
                if (tgtContextMethod != null && !callStack.contains(tgtContextMethod)) {
                    // do call graph edge append
                    super.flowEngine.addCGEdge(callNode);

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
                    if (tgtMethod.isAbstract() && tgtContextMethod.getTaintContainer().isParamTaint()) {
                        retIsTaint = true;
                        continue;
                    }

                    // traverse into target method
                    super.flowEngine.traverse(tgtContextMethod);
                    // if target method has body, will traverse it
                    if (!retIsTaint && tgtContextMethod.returnTaint()) {
                        retIsTaint = true;
                    }
                }
            }
        }
        return retIsTaint;
    }
}
