package org.example.neo4j.service;

import org.example.config.NodeRepository;
import org.example.constant.InvokeType;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Neo4jNode;
import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayReference;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.node.VoidNode;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.neo4j.context.ContextMethod;
import org.example.neo4j.context.InstanceContextMethod;
import org.example.neo4j.context.SpecialContextMethod;
import org.example.neo4j.context.StaticContextMethod;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.FieldAlloc;
import org.example.neo4j.node.var.ObjAlloc;
import org.example.neo4j.node.var.StaticAlloc;
import org.example.neo4j.relation.*;
import org.example.util.Log;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.util.*;

public class FlowEngine {
    private Map<String, IntraAnalyzedMethod> analyzedMethodMap;
    private CallGraph cg;
    private Set<AbstractRelation> relations;

    private Stack<ContextMethod> stack;
    private Hierarchy hierarchy;

    private Set<Node> taintNodes;

    public FlowEngine(CallGraph cg, Map<String, IntraAnalyzedMethod> analyzedMethodMap, Set<Node> nodes) {
        this.cg = cg;
        this.analyzedMethodMap = analyzedMethodMap;
        this.stack = new Stack<>();
        this.relations = new HashSet<>();
        this.hierarchy = Scene.v().getActiveHierarchy();
        this.taintNodes = nodes;
    }

    public void buildRelations() {
        Scene.v().getEntryPoints().forEach(sootMethod -> {
            Obj fakeObj = new PhantomObj(sootMethod.getDeclaringClass());
            ContextMethod entry = new InstanceContextMethod(fakeObj, sootMethod, null, null);
            traverseV2(entry);
        });
    }

    public void traverseV2(ContextMethod entry) {
        stack.push(entry);
        addCallNodeRelation(entry.getCallNode());

        IntraAnalyzedMethod analyzedMethod = analyzedMethodMap.get(entry.getSootMethod().getSignature());
        if (analyzedMethod != null) {
            // add method has relation
            Set<MethodLevelSite> methodLocalVariables = NodeRepository.getMethodNodes(analyzedMethod.getSignature());
            AbstractMethod abstractMethod = AbstractMethod.getInstance(analyzedMethod.getMethodRef());
            for (MethodLevelSite variable : methodLocalVariables) {
                relations.add(new HasVar(abstractMethod, Objects.requireNonNull(getInstance(variable))));
            }
            analyzedMethod.getOrderedFlowMap().forEach((node, nodes) -> {
                for (Node from : nodes) {
                    if (from instanceof Obj) {
                        addPointRelation(node, from);
                    } else if (from instanceof CallNode) {
                        CallNode callNode = (CallNode) from;
                        Unit callSite = callNode.getCallSite();
                        for (SootMethod tgtMethod : dispatch(callNode)) {
                            callNode.resetUnifyRet(tgtMethod);
                            Call call = new Call(entry.getSootMethod(), tgtMethod, callSite);
                            relations.add(call);
                            ContextMethod tgtContextMethod = null;
                            if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
                                // if this invoke is a instance invoke, will get obj
                                Node baseNode = callNode.getBase();
                                // if base node is this, will get last call stack obj
                                Obj obj = getBaseRefObj(baseNode);
                                tgtContextMethod = new InstanceContextMethod(obj, tgtMethod, callNode, callSite);
                            } else if (callNode.getInvokeType() == InvokeType.STATIC_INVOKE) {
                                assert tgtMethod != null;
                                tgtContextMethod = new StaticContextMethod(tgtMethod.getDeclaringClass(), tgtMethod, callNode, callSite);
                            } else if (callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
                                Obj obj = getBaseRefObj(callNode.getBase());
                                tgtContextMethod = new SpecialContextMethod(obj, tgtMethod, callNode, callSite);
                            }

                            if (!stack.contains(tgtContextMethod)) {
                                traverseV2(tgtContextMethod);
                            }
                        }
                    } else {
                        addTaintRelation(node, from);
                    }
                }
            });
        }

        stack.pop();
    }

    public void addCallNodeRelation(CallNode callNode) {
        if (callNode != null) {
            AbstractMethod callee = AbstractMethod.getInstance(callNode.getCallee());
            List<Node> params = callNode.getParams();
            List<Node> args = callNode.getArgs();
            assert params.size() == args.size();
            for (int i = 0; i < params.size(); i++) {
                AbstractAllocNode paramNode = getInstance(params.get(i));
                AbstractAllocNode argNode = getInstance(args.get(i));
                assert paramNode != null && argNode != null;
                relations.add(new Taint(argNode, paramNode));
                relations.add(new HasVar(callee, paramNode));
            }

            // add call return relation
            if (callNode.getRetVar() != null && !(callNode.getRetVar() instanceof VoidNode)) {
                AbstractAllocNode retNode = getInstance(callNode.getRetVar());
                AbstractAllocNode unifyRetNode = getInstance(callNode.getUnifyRet());
                assert retNode != null && unifyRetNode != null;
                relations.add(new Taint(unifyRetNode, retNode));
                relations.add(new HasVar(callee, unifyRetNode));
            }
        }
    }

    public Obj getBaseRefObj(Node node) {
        Obj obj = null;
        // rebase this variable
        if (node instanceof ThisVariable) {
            obj = getLastStackObj();
        }
        // get point to relation
        if (obj == null) {
            obj = NodeRepository.getPointObj(node);
        }
        // can't find a obj, will make a phantom obj
        if (obj == null) {
            if (node instanceof LocalVariable) {
                SootClass sootClass = Scene.v().getSootClass(((LocalVariable) node).getType());
                obj = new PhantomObj(sootClass);
            }
        }
        return obj;
    }

    public Obj getLastStackObj() {
        ContextMethod contextMethod = stack.peek();
        if (contextMethod instanceof InstanceContextMethod) {
            return ((InstanceContextMethod) contextMethod).getObj();
        }
        return null;
    }

    public void saveRelationsToNeo4j() {
        Neo4jService neo4jService = new Neo4jService();
        neo4jService.saveRelation(relations);
    }

    private Set<SootMethod> dispatch(CallNode callNode) {
        Set<SootMethod> sootMethodSet = new HashSet<>();
        String methodSubSig = callNode.getSubSignature();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            LocalVariable base = (LocalVariable) callNode.getBase();
            Obj obj = getBaseRefObj(base);
            // if obj is a phantom object, will reduce to CHA algorithm
            if (obj instanceof PhantomObj) {
                SootClass sootClass = Scene.v().loadClassAndSupport(base.getType());
                List<SootClass> classes = new ArrayList<>();
                if (sootClass.isInterface()) {
                    classes.addAll(hierarchy.getImplementersOf(sootClass));
                } else {
                    classes.addAll(hierarchy.getSubclassesOfIncluding(sootClass));
                }
                for (SootClass chaClass : classes) {
                    sootMethodSet.addAll(dispatch(chaClass, methodSubSig));
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

    public Set<SootMethod> dispatch(SootClass sootClass, String methodSubSig) {
        Set<SootMethod> tgtMethods = new HashSet<>();

        SootMethod tgtMethod = sootClass.getMethodUnsafe(methodSubSig);
        if (tgtMethod == null || tgtMethod.isAbstract()) {
            List<SootClass> directSuperClass = hierarchy.getSuperclassesOf(sootClass);
            for (SootClass superClass : directSuperClass) {
                tgtMethods.addAll(dispatch(superClass, methodSubSig));
            }
        } else {
            tgtMethods.add(tgtMethod);
        }

        return tgtMethods;
    }

    public void addPointRelation(Node to, Node from) {
        AbstractAllocNode toNode = getInstance(to);
        AbstractAllocNode fromNode = getInstance(from);
        relations.add(new PointTo(Objects.requireNonNull(toNode), (ObjAlloc) Objects.requireNonNull(fromNode)));
    }

    public AbstractAllocNode getInstance(Node node) {
        AbstractAllocNode allocNode;
        if (node instanceof InstanceField) {
            allocNode = rebaseThisRef((InstanceField) node);
        } else if (node instanceof ArrayReference) {
            allocNode = rebaseArrayRef((ArrayReference) node);
        } else if (node instanceof StaticField) {
            allocNode = rebaseStaticRef((StaticField) node);
        } else if (node instanceof Neo4jNode) {
            allocNode = ((Neo4jNode) node).convert();
        } else {
            Log.error("%s node not support to convert neo4j node", node);
            return null;
        }
        if (taintNodes.contains(node)) {
            allocNode.setTaint(true);
        }
        return allocNode;
    }

    public AbstractAllocNode rebaseThisRef(InstanceField instanceField) {
        Obj obj;
        if (instanceField.getBase() instanceof ThisVariable) {
            InstanceContextMethod methodContext = (InstanceContextMethod) stack.peek();
            obj = methodContext.getObj();
        } else {
            obj = NodeRepository.getPointObj(instanceField.getBase());
        }

        AbstractAllocNode fieldNode = new FieldAlloc(obj.getID(), instanceField.getFieldRef());
        AbstractAllocNode objNode = getInstance(obj);
        assert objNode != null;
        relations.add(new FieldTo(objNode, fieldNode));
        return fieldNode;
    }

    public AbstractAllocNode rebaseStaticRef(StaticField staticField) {
        AbstractAllocNode fieldNode = new FieldAlloc(staticField.getType(), staticField.getFieldRef());
        AbstractAllocNode classNode = new StaticAlloc(staticField.getFieldRef().getDeclaringClass());
        relations.add(new FieldTo(classNode, fieldNode));
        return fieldNode;
    }

    public AbstractAllocNode rebaseArrayRef(ArrayReference arrayReference) {
        AbstractAllocNode baseNode = getInstance(arrayReference.getBaseNode());
//        AbstractAllocNode idxNode = getInstance(arrayReference.getIdxNode());
//        relations.add(new IdxTo(baseNode, idxNode));
        return baseNode;
    }

    public void addTaintRelation(Node to, Node from) {
        AbstractAllocNode toNode = getInstance(to);
        AbstractAllocNode fromNode = getInstance(from);
        relations.add(new Taint(Objects.requireNonNull(fromNode), Objects.requireNonNull(toNode)));
    }
}
