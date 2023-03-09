package org.example.flow.handler.impl;

import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.core.expr.AbstractExprNode;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;
import org.example.flow.TaintContainer;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.SingleExprHandler;
import soot.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeCallNodeHandler extends SingleExprHandler {
    public NodeCallNodeHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public boolean canHandle(Node to, AbstractExprNode from) {
        // handle: a = call();
        return super.canHandle(to, from) && super.getNode(from) instanceof CallNode;
    }

    @Override
    public void handle(Node to, AbstractExprNode from) {
        CallNode callNode = (CallNode) super.getNode(from);
        handleCallNode(to, callNode);
    }

    public void handleCallNode(Node to, CallNode callNode) {
        CallStack callStack = super.flowEngine.getCallStack();
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
                super.flowEngine.addCGEdge(callNode);
                if (super.flowEngine.traverse(tgtContextMethod)) {
                    super.getTaintContainer().addTaint(to);
                }
            }
        }
    }

    private Set<SootMethod> dispatch(CallNode callNode) {
        CallStack callStack = super.flowEngine.getCallStack();
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
