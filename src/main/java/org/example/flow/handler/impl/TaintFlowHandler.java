package org.example.flow.handler.impl;

import org.example.config.Global;
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
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.EmptyExprNode;
import org.example.core.expr.OpExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.rule.Sink;
import org.example.util.Log;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaintFlowHandler extends AbstractFlowHandler {
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

    public boolean isRightTaint(EmptyExprNode from) {
        return false;
    }

    public void addLeftTaint(Node node) {
        if (node instanceof LocalVariable) {
            getTaintContainer().addTaint(node);
        } else if (node instanceof InstanceField) {
            InstanceField field = (InstanceField) node;
            Obj baseObj = callStack.getBaseRefObj(field.getBase());
            baseObj.putInstanceField(field, true);
        } else if (node instanceof StaticField) {
            getTaintContainer().addTaint(node);
        } else if (node instanceof UnifyReturn) {
            getTaintContainer().addTaint(node);
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
            Obj baseObj = callStack.getBaseRefObj(base);
            return baseObj.isTaintField(field);
        }
        return false;
    }

    private boolean handleCallNode(CallNode callNode) {
        Unit callSite = callNode.getCallSite();
        for (SootMethod tgtMethod : dispatch(callNode)) {
            callNode.resetUnifyRet(tgtMethod);
            ContextMethod tgtContextMethod = null;
            if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
                // if this invokes is a instance invoke, will get obj
                Node baseNode = callNode.getBase();
                // if base node is this, will get last call stack obj
                Obj obj = callStack.getBaseRefObj(baseNode);
                callNode.setThisRef(obj);
                tgtContextMethod = new InstanceContextMethod(obj, tgtMethod, callNode, callSite);
            } else if (callNode.getInvokeType() == InvokeType.STATIC_INVOKE) {
                tgtContextMethod = new StaticContextMethod(tgtMethod.getDeclaringClass(), tgtMethod, callNode, callSite);
            } else if (callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
                Obj obj = callStack.getBaseRefObj(callNode.getBase());
                callNode.setThisRef(obj);
                tgtContextMethod = new SpecialContextMethod(obj, tgtMethod, callNode, callSite);
            }

            if (tgtContextMethod != null && !callStack.contains(tgtContextMethod)) {
                // do call graph edge append
                super.flowEngine.addCGEdge(callNode);

                Sink sink = Global.sinkMap.get(tgtContextMethod.getSootMethod().getSignature());

                // do arg param taint map
                List<Node> args = callNode.getArgs();
                if (args != null && args.size() > 0) {
                    for (int i = 0; i < args.size(); i++) {
                        if (getTaintContainer().containsTaint(args.get(i))) {
                            if (sink != null && sink.index.contains(i)) {
                                Log.info("!!! find vulnerability reach sink to %s", tgtContextMethod.getSootMethod().getSignature());
                            }
                            tgtContextMethod.getTaintContainer().addTaint(tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i));
                        }
                    }
                }

                return super.flowEngine.traverse(tgtContextMethod);
            }
        }
        return false;
    }


    private Set<SootMethod> dispatch(CallNode callNode) {
        Set<SootMethod> sootMethodSet = new HashSet<>();
        String methodSubSig = callNode.getSubSignature();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            LocalVariable base = (LocalVariable) callNode.getBase();
            Obj obj = callStack.getBaseRefObj(base);
            // if obj is a phantom object, will reduce to CHA algorithm
            if (obj instanceof PhantomObj) {
                SootClass sootClass = Scene.v().loadClassAndSupport(base.getType());
                List<SootClass> classes = new ArrayList<>();
                if (sootClass.isInterface()) {
                    classes.addAll(super.flowEngine.getHierarchy().getImplementersOf(sootClass));
                } else {
                    classes.addAll(super.flowEngine.getHierarchy().getSubclassesOfIncluding(sootClass));
                }

                if (classes.isEmpty()) {
                    sootMethodSet.add(sootClass.getMethodUnsafe(methodSubSig));
                } else {
                    for (SootClass chaClass : classes) {
                        sootMethodSet.addAll(dispatch(chaClass, methodSubSig));
                    }
                }
            } else {
                SootClass sootClass = Scene.v().loadClassAndSupport(obj.getType());
                sootMethodSet.addAll(dispatch(sootClass, methodSubSig));
            }
        } else {
            sootMethodSet.add(callNode.getCallee());
        }
        return sootMethodSet;
    }

    private Set<SootMethod> dispatch(SootClass sootClass, String methodSubSig) {
        Set<SootMethod> tgtMethods = new HashSet<>();

        SootMethod tgtMethod = sootClass.getMethodUnsafe(methodSubSig);
        if (tgtMethod == null || tgtMethod.isAbstract()) {
            List<SootClass> directSuperClass = super.flowEngine.getHierarchy().getSuperclassesOf(sootClass);
            for (SootClass superClass : directSuperClass) {
                tgtMethods.addAll(dispatch(superClass, methodSubSig));
            }
        } else {
            tgtMethods.add(tgtMethod);
        }

        return tgtMethods;
    }
}
