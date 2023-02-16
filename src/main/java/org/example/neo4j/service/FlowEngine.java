package org.example.neo4j.service;

import org.example.config.NodeRepository;
import org.example.constant.InvokeType;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.NormalObj;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.neo4j.context.ContextMethod;
import org.example.neo4j.context.InstanceContextMethod;
import org.example.neo4j.context.SpecialContextMethod;
import org.example.neo4j.context.StaticContextMethod;
import org.example.neo4j.relation.AbstractRelation;
import org.example.neo4j.relation.Call;
import org.example.util.Log;
import reactor.core.Fuseable;
import soot.*;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.*;
import java.util.function.Consumer;

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
            ContextMethod entry = new InstanceContextMethod(fakeObj, sootMethod, null);
            traverse(entry);
        });
    }

    public void traverse(ContextMethod entry) {
        stack.push(entry);

        IntraAnalyzedMethod analyzedMethod = analyzedMethodMap.get(entry.getSootMethod().getSignature());
        RelationExtractor relationExtractor = new RelationExtractor(entry.getCallNode(), entry.getCallSite(), entry.getSootMethod(), analyzedMethod, stack, taintNodes);
        relationExtractor.addRelation();
        relations.addAll(relationExtractor.getRelations());

        if (analyzedMethod == null) {
            if (entry.getSootMethod().getDeclaringClass().isApplicationClass()) {
                Log.warn("method %s doesn't have intra analysis result, will skip analysis", entry.getSootMethod().getSignature());
            }
        } else {
            List<CallNode> callNodes = analyzedMethod.getCallNodes();
            for (CallNode callNode : callNodes) {
                Unit callSite = callNode.getCallSite();
                SootMethod tgtMethod = dispatch(callNode);

                Call call = new Call(entry.getSootMethod(), tgtMethod, callSite);
                relations.add(call);

                ContextMethod tgtContextMethod = null;
                if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
                    // if this invoke is a instance invoke, will get obj
                    Node baseNode = callNode.getBase();
                    // if base node is this, will get last call stack obj
                    Obj obj = getBaseRefObj(baseNode);
                    tgtContextMethod = new InstanceContextMethod(obj, tgtMethod, callSite);
                } else if (callNode.getInvokeType() == InvokeType.STATIC_INVOKE) {
                    assert tgtMethod != null;
                    tgtContextMethod = new StaticContextMethod(tgtMethod.getDeclaringClass(), tgtMethod, callSite);
                } else if (callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
                    Obj obj = getBaseRefObj(callNode.getBase());
                    tgtContextMethod = new SpecialContextMethod(obj, tgtMethod, callSite);
                }

                if (!stack.contains(tgtContextMethod)) {
                    traverse(tgtContextMethod);
                }
            }
        }


        stack.pop();
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

    private SootMethod dispatch(CallNode callNode) {
        String methodSubSig = callNode.getSubSignature();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
            LocalVariable base = (LocalVariable) callNode.getBase();
            Obj obj = getBaseRefObj(base);
            if (obj instanceof PhantomObj) {
                SootClass sootClass = Scene.v().loadClassAndSupport(base.getType());
                for (SootClass superClass : hierarchy.getSuperclassesOfIncluding(sootClass)) {
                    SootMethod sootMethod = superClass.getMethodUnsafe(methodSubSig);
                    if (sootMethod != null) {
                        return sootMethod;
                    }
                }
                return null;
            }
            SootClass sootClass = Scene.v().loadClassAndSupport(obj.getType());
            return sootClass.getMethodUnsafe(methodSubSig);
        } else {
            return callNode.getCallee();
        }
    }
}
