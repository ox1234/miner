package org.example.flow.handler.impl;

import fj.data.fingertrees.Single;
import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.ObjField;
import org.example.core.basic.obj.PhantomObj;
import org.example.core.expr.*;
import org.example.flow.FlowEngine;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;

public class PointFlowHandler extends AbstractFlowHandler<Set<Obj>> {

    public PointFlowHandler(FlowEngine flowEngine) {
        super(flowEngine);
    }

    @Override
    public Set<Obj> handleEmptyExprNode(EmptyExprNode from) {
        return Collections.emptySet();
    }

    @Override
    public Set<Obj> handleOpExprNode(OpExprNode from) {
        return Collections.emptySet();
    }

    @Override
    public Set<Obj> handleMultiExprNode(MultiExprNode from) {
        Set<Obj> refObjs = new LinkedHashSet<>();
        for (Node node : from.getAllNodes()) {
            refObjs.addAll(getNodeRefObj(node));
        }
        return refObjs;
    }

    @Override
    public Set<Obj> handleSingleExprNode(SingleExprNode from) {
        Set<Obj> resObj = new LinkedHashSet<>();
        Node node = from.getFirstNode();
        if (node instanceof CallNode) {
            Set<ContextMethod> tgtContextMethods = handleCallNode((CallNode) node);
            for (ContextMethod contextMethod : tgtContextMethods) {
                resObj.addAll(contextMethod.getPointToContainer().getReturnObjs());
            }
        } else {
            resObj.addAll(getNodeRefObj(node));
        }
        return resObj;
    }

    @Override
    public void transferLeft(Node to, Set<Obj> from) {
        if (to instanceof LocalVariable) {
            getPointContainer().addPointRelation(to, from);
        } else if (to instanceof InstanceField) {
            InstanceField field = (InstanceField) to;
            Set<Obj> baseObjs = callStack.getBaseRefObj(field.getBase());
            for (Node obj : from) {
                for (Obj baseObj : baseObjs) {
                    baseObj.putInstanceField(field, obj);
                }
            }
        } else if (to instanceof Parameter) {
            getPointContainer().addPointRelation(to, from);
        } else if (to instanceof UnifyReturn) {
            getPointContainer().addPointRelation(to, from);
        }
    }

    @Override
    protected Set<ContextMethod> handleCallNode(CallNode callNode) {
        Set<ContextMethod> calleeContextMethods = new LinkedHashSet<>();
        for (SootMethod tgtMethod : dispatch(callNode)) {
            for (ContextMethod tgtContextMethod : getTargetContextMethod(callNode, tgtMethod)) {
                // add to call graph
                flowEngine.getCtxCG().addCtxCGEdge(callStack.peek(), tgtContextMethod);
                flowEngine.getCtxCG().addCallNodeDispatch(callNode, tgtContextMethod);

                // add to returned context methods
                calleeContextMethods.add(tgtContextMethod);

                // build call graph, manual call pre and post handler
                preProcessCallNode(callNode, tgtContextMethod);
                flowEngine.buildCG(tgtContextMethod);
                postProcessCallNode(callNode, tgtContextMethod);
            }
        }
        return calleeContextMethods;
    }

    public void preProcessCallNode(CallNode callNode, ContextMethod tgtContextMethod) {
        // map param - arg point relation
        for (int i = 0; i < callNode.getArgs().size(); i++) {
            Node argNode = callNode.getArgs().get(i);
            Node paramNode = tgtContextMethod.getIntraAnalyzedMethod().getParameterNodes().get(i);
            tgtContextMethod.getPointToContainer().addLocalPointRelation(paramNode, getNodeRefObj(argNode));
        }
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
