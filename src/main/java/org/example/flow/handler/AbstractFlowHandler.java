package org.example.flow.handler;

import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;
import org.example.flow.PointToContainer;
import org.example.flow.TaintContainer;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;

abstract public class AbstractFlowHandler implements FlowHandler {
    protected FlowEngine flowEngine;
    protected CallStack callStack;

    protected AbstractFlowHandler(FlowEngine flowEngine) {
        this.flowEngine = flowEngine;
        this.callStack = flowEngine.getCallStack();
    }

    public TaintContainer getTaintContainer() {
        return callStack.peek().getTaintContainer();
    }

    public PointToContainer getPointContainer() {
        return callStack.peek().getPointToContainer();
    }

    protected Set<ContextMethod> getTargetContextMethod(CallNode callNode, SootMethod tgtMethod) {
        callNode.resetUnifyRet(tgtMethod);
        Set<ContextMethod> tgtContextMethods = new LinkedHashSet<>();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            // if this invokes is a instance invoke, will get obj
            Node baseNode = callNode.getBase();
            // if base node is this, will get last call stack obj
            Set<Obj> objs = callStack.getBaseRefObj(baseNode);
            for (Obj obj : objs) {
                callNode.setThisRef(obj);
                ContextMethod tgtContextMethod = new InstanceContextMethod(obj, tgtMethod, callNode, callNode.getCallSite());
                tgtContextMethods.add(tgtContextMethod);
            }
        } else if (callNode.getInvokeType() == InvokeType.STATIC_INVOKE) {
            ContextMethod tgtContextMethod = new StaticContextMethod(tgtMethod.getDeclaringClass(), tgtMethod, callNode, callNode.getCallSite());
            tgtContextMethods.add(tgtContextMethod);
        } else if (callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
            Set<Obj> objs = callStack.getBaseRefObj(callNode.getBase());
            for (Obj obj : objs) {
                callNode.setThisRef(obj);
                ContextMethod tgtContextMethod = new SpecialContextMethod(obj, tgtMethod, callNode, callNode.getCallSite());
                tgtContextMethods.add(tgtContextMethod);
            }
        }
        return tgtContextMethods;
    }


    protected Set<SootMethod> dispatch(CallNode callNode) {
        Set<SootMethod> sootMethodSet = new HashSet<>();
        String methodSubSig = callNode.getSubSignature();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            LocalVariable base = (LocalVariable) callNode.getBase();
            Set<Obj> objs = callStack.getBaseRefObj(base);
            // if obj is a phantom object, will reduce to CHA algorithm
            for (Obj obj : objs) {
                if (obj instanceof PhantomObj) {
                    SootClass sootClass = Scene.v().loadClassAndSupport(base.getType());
                    List<SootClass> classes = new ArrayList<>();
                    if (sootClass.isInterface()) {
                        classes.addAll(flowEngine.getHierarchy().getImplementersOf(sootClass));
                    } else {
                        classes.addAll(flowEngine.getHierarchy().getSubclassesOfIncluding(sootClass));
                    }

                    if (classes.isEmpty()) {
                        sootMethodSet.addAll(dispatch(sootClass, methodSubSig));
                    } else {
                        for (SootClass chaClass : classes) {
                            sootMethodSet.addAll(dispatch(chaClass, methodSubSig));
                        }
                    }
                } else {
                    SootClass sootClass = Scene.v().loadClassAndSupport(obj.getType());
                    sootMethodSet.addAll(dispatch(sootClass, methodSubSig));
                }
            }
        } else {
            sootMethodSet.add(callNode.getCallee());
        }
        return sootMethodSet;
    }

    protected Set<SootMethod> dispatch(SootClass sootClass, String methodSubSig) {
        Set<SootMethod> tgtMethods = new HashSet<>();

        SootMethod tgtMethod = sootClass.getMethodUnsafe(methodSubSig);
        if (tgtMethod == null) {
            List<SootClass> directSuperClass = getDirectSuperClasses(sootClass);
            for (SootClass superClass : directSuperClass) {
                tgtMethods.addAll(dispatch(superClass, methodSubSig));
            }
        } else {
            tgtMethods.add(tgtMethod);
        }

        return tgtMethods;
    }

    protected List<SootClass> getDirectSuperClasses(SootClass targetClass) {
        List<SootClass> directSuperClasses = new ArrayList<>();
        if (targetClass.hasSuperclass()) {
            directSuperClasses.add(targetClass.getSuperclass());
        }
        directSuperClasses.addAll(targetClass.getInterfaces());
        return directSuperClasses;
    }
}
