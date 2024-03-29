package org.example.flow.handler.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.field.ArrayLoad;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.ConstantObj;
import org.example.core.basic.obj.MapCollectionObj;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.core.expr.EmptyExprNode;
import org.example.core.expr.MultiExprNode;
import org.example.core.expr.OpExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.core.hook.bytedance.proto.Call;
import org.example.flow.collector.Collector;
import org.example.flow.FlowEngine;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.flow.context.SpecialContextMethod;
import org.example.flow.context.StaticContextMethod;
import org.example.flow.handler.AbstractFlowHandler;
import org.example.util.NodeUtil;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;

import java.util.*;
import java.util.function.Consumer;

public class PointFlowHandler extends AbstractFlowHandler<Set<Obj>> {
    private final Logger logger = LogManager.getLogger(PointFlowHandler.class);
    private Map<SootMethod, Set<SootMethod>> dispathMap = new HashMap<>();

    public PointFlowHandler(FlowEngine flowEngine, Collector... collectors) {
        super(flowEngine, collectors);
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
            refObjs.addAll(callStack.getRefObjs(node));
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
                // handle collection method
                if (isCollectionMethod(contextMethod.getSootMethod())) {
                    resObj.addAll(handleCollectionCall(contextMethod));
                } else {
                    // handle normal method
                    if (contextMethod.getSootMethod().getDeclaringClass().isApplicationClass()) {
                        resObj.addAll(contextMethod.getPointToContainer().getReturnObjs());
                    } else if (!(contextMethod instanceof SpecialContextMethod) && !contextMethod.getSootMethod().getReturnType().equals(VoidType.v())) {
                        Node objNode = Site.getNodeInstance(PhantomObj.class, contextMethod.getSootMethod().getReturnType(), ((CallNode) node).getRetVar());
                        resObj.add((Obj) objNode);
                    }
                }
            }
        } else if (node instanceof InstanceField) {
            InstanceField instanceField = (InstanceField) node;
            Set<Obj> baseObjs = callStack.getRefObjs(instanceField.getBase());
            baseObjs.forEach(obj -> resObj.addAll(obj.getField(instanceField.getField().getName())));
        } else {
            resObj.addAll(callStack.getRefObjs(node));
        }
        return resObj;
    }

    @Override
    public void transferLeft(Node to, Set<Obj> from) {
        if (to instanceof LocalVariable) {
            getPointContainer().addPointRelation(to, from);
        } else if (to instanceof InstanceField) {
            InstanceField field = (InstanceField) to;
            Set<Obj> baseObjs = callStack.getRefObjs(field.getBase());
            handleFieldStore(baseObjs, Collections.singleton(field.getField().getName()), from);
        } else if (to instanceof StaticField) {
            getPointContainer().addPointRelation(to, from);
        } else if (to instanceof ArrayLoad) {
            ArrayLoad arrLoad = (ArrayLoad) to;
            Set<Obj> baseObjs = callStack.getRefObjs(arrLoad.getBaseNode());
            Set<Obj> idxObjs = callStack.getRefObjs(arrLoad.getIdxNode());
            Set<String> fieldIDs = new HashSet<>();
            idxObjs.forEach(obj -> {
                if (obj instanceof ConstantObj) {
                    fieldIDs.add(((ConstantObj) obj).getValue());
                }
            });
            handleFieldStore(baseObjs, fieldIDs, from);
        }
    }

    public void handleFieldStore(Set<Obj> baseOBjs, Set<String> fieldIDs, Set<Obj> storeObjs) {
        for (Obj baseObj : baseOBjs) {
            for (String fieldID : fieldIDs) {
                baseObj.putField(fieldID, storeObjs);
            }
        }
    }

    protected Set<Obj> handleCollectionCall(ContextMethod contextMethod) {
        Set<Obj> retObjs = new LinkedHashSet<>();
        CallNode callNode = contextMethod.getCallNode();
        if (callNode.getCallee().isJavaLibraryMethod()) {
            Set<Obj> baseNodes = getPointContainer().getPointRefObj(callNode.getBase());
            baseNodes.forEach(obj -> {
                if (obj instanceof MapCollectionObj) {
                    Set<Obj> baseObjs = getPointContainer().getPointRefObj(callNode.getBase());
                    baseObjs.forEach(baseObj -> retObjs.addAll(handleMapCall(callNode.getCallee().getName(), baseObj, callNode.getArgs())));
                }
            });
        }
        return retObjs;
    }

    protected Set<Obj> handleMapCall(String callName, Obj collectionObj, List<Node> args) {
        Set<Obj> retObjs = new LinkedHashSet<>();
        switch (callName) {
            case "put":
            case "putIfAbsent":
                Set<Obj> putKeyObjs = getPointContainer().getNodeRefObj(args.get(0));
                Set<Obj> putValObjs = getPointContainer().getNodeRefObj(args.get(1));
                putKeyObjs.forEach(keyObj -> retObjs.addAll(handleMapPut(collectionObj, keyObj, putValObjs)));
                break;
            case "get":
                Set<Obj> getKeyObjs = getPointContainer().getNodeRefObj(args.get(0));
                getKeyObjs.forEach(keyObj -> retObjs.addAll(handleMapGet(collectionObj, keyObj)));
                break;
        }
        return retObjs;
    }

    protected Set<Obj> handleMapPut(Obj mapObj, Obj key, Set<Obj> val) {
        MapCollectionObj mapCollectionObj = (MapCollectionObj) mapObj;
        mapCollectionObj.putKV(key, val);
        return Collections.emptySet();
    }

    protected Set<Obj> handleMapGet(Obj mapObj, Obj key) {
        MapCollectionObj mapCollectionObj = (MapCollectionObj) mapObj;
        return mapCollectionObj.getKV(key);
    }

    @Override
    protected Set<ContextMethod> handleCallNode(CallNode callNode) {
        Set<ContextMethod> calleeContextMethods = new LinkedHashSet<>();
        Set<SootMethod> targetMethods = dispatch(callNode);
        if (targetMethods.size() > 1) {
            dispathMap.put(callNode.getCallee(), targetMethods);
        }
        for (SootMethod tgtMethod : targetMethods) {
            for (ContextMethod tgtContextMethod : getTargetContextMethod(callNode, tgtMethod)) {
                if (callStack.contains(tgtContextMethod)) {
                    continue;
                }
                // add to call graph
                flowEngine.getCtxCG().addCtxCGEdge(callStack.peek(), tgtContextMethod);
                flowEngine.getCtxCG().addCallNodeDispatch(callNode, tgtContextMethod);

                // add to returned context methods
                calleeContextMethods.add(tgtContextMethod);

                // build call graph, manual call pre and post handler
                preProcessCallNode(callNode, tgtContextMethod);
                doAnalysis(tgtContextMethod);
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
            tgtContextMethod.getPointToContainer().addLocalPointRelation(paramNode, callStack.getRefObjs(argNode));
        }
    }

    protected Set<ContextMethod> getTargetContextMethod(CallNode callNode, SootMethod tgtMethod) {
        callNode.resetUnifyRet(tgtMethod);
        Set<ContextMethod> tgtContextMethods = new LinkedHashSet<>();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            // if this invokes is a instance invoke, will get obj
            Node baseNode = callNode.getBase();
            // if base node is this, will get last call stack obj
            Set<Obj> objs = callStack.getRefObjs(baseNode);
            for (Obj obj : objs) {
                callNode.setThisRef(obj);
                ContextMethod tgtContextMethod = new InstanceContextMethod(obj, tgtMethod, callNode, callNode.getCallSite());
                tgtContextMethods.add(tgtContextMethod);
            }
        } else if (callNode.getInvokeType() == InvokeType.STATIC_INVOKE) {
            ContextMethod tgtContextMethod = new StaticContextMethod(tgtMethod.getDeclaringClass(), tgtMethod, callNode, callNode.getCallSite());
            tgtContextMethods.add(tgtContextMethod);
        } else if (callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
            Set<Obj> objs = callStack.getRefObjs(callNode.getBase());
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
        if (callNode.getCallee().getDeclaringClass().getName().equals("java.lang.Object")) {
            sootMethodSet.add(callNode.getCallee());
            return sootMethodSet;
        }

        String methodSubSig = callNode.getSubSignature();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            LocalVariable base = (LocalVariable) callNode.getBase();
            Set<Obj> objs = callStack.getRefObjs(base);
            // if obj is a phantom object, will reduce to CHA algorithm
            for (Obj obj : objs) {
                if (obj instanceof PhantomObj) {
                    SootClass sootClass = Scene.v().loadClassAndSupport(base.getType().toString());
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
                    SootClass sootClass = Scene.v().loadClassAndSupport(obj.getType().toString());
                    sootMethodSet.addAll(dispatch(sootClass, methodSubSig));
                }
            }
        } else {
            sootMethodSet.add(callNode.getCallee());
        }

        sootMethodSet.removeIf(SootMethod::isJavaLibraryMethod);
        if (sootMethodSet.isEmpty()) {
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
