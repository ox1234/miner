package org.example.neo4j.service;

import org.example.config.NodeRepository;
import org.example.constant.InvokeType;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.NormalObj;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.context.ContextMethod;
import org.example.neo4j.context.InstanceContextMethod;
import org.example.neo4j.context.SpecialContextMethod;
import org.example.neo4j.context.StaticContextMethod;
import org.example.neo4j.relation.AbstractRelation;
import org.example.neo4j.relation.Call;
import org.example.util.Log;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.*;

public class FlowEngine {
    private Map<String, IntraAnalyzedMethod> analyzedMethodMap;
    private CallGraph cg;
    private Set<AbstractRelation> relations;

    private Stack<ContextMethod> stack;

    public FlowEngine(CallGraph cg, Map<String, IntraAnalyzedMethod> analyzedMethodMap) {
        this.cg = cg;
        this.analyzedMethodMap = analyzedMethodMap;
        this.stack = new Stack<>();
        this.relations = new HashSet<>();
    }

    public void buildRelations() {
        Scene.v().getEntryPoints().forEach(sootMethod -> {
            Obj fakeObj = new NormalObj(sootMethod.getDeclaringClass());
            ContextMethod entry = new InstanceContextMethod(fakeObj, sootMethod, null);
            traverse(entry);
        });
    }

    public void traverse(ContextMethod entry) {
        stack.push(entry);

        RelationExtractor relationExtractor = new RelationExtractor(entry.getCallNode(), entry.getCallSite(), entry.getSootMethod(), analyzedMethodMap.get(entry.getSootMethod().getSignature()), stack);
        relationExtractor.addRelation();
        relations.addAll(relationExtractor.getRelations());

        for (Iterator<Edge> it = cg.edgesOutOf(entry.getSootMethod()); it.hasNext(); ) {
            Edge edge = it.next();

            SootMethod srcMethod = edge.src();
            SootMethod tgtMethod = edge.tgt();
            if (tgtMethod == null || tgtMethod.getName().equals("<clinit>") || !srcMethod.getDeclaringClass().isApplicationClass()) {
                continue;
            }

            Log.info("Visiting: %s -> %s with stmt %s", srcMethod.getSignature(), tgtMethod.getSignature(), edge.srcStmt());

            Stmt callSite = edge.srcStmt();
            String callNodeID = CallNode.getCallNodeID(callSite);
            CallNode callNode = (CallNode) NodeRepository.getNode(callNodeID);

            // add call edges
            relations.add(new Call(srcMethod, tgtMethod, callSite));

            // handle instance invoke expr
            ContextMethod tgtContextMethod = null;
            if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE) {
                // if this invoke is a instance invoke, will get obj
                Node baseNode = callNode.getBase();
                // if base node is this, will get last call stack obj
                Obj obj = null;
                if (baseNode instanceof ThisVariable) {
                    obj = getLastStackObj();
                } else {
                    obj = getBaseRefObj(baseNode);
                }
                tgtContextMethod = new InstanceContextMethod(obj, tgtMethod, callSite);
            } else if (callNode.getInvokeType() == InvokeType.STATIC_INVOKE) {
                tgtContextMethod = new StaticContextMethod(tgtMethod.getDeclaringClass(), tgtMethod, callSite);
            } else if (callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
                Obj obj = getBaseRefObj(callNode.getBase());
                tgtContextMethod = new SpecialContextMethod(obj, tgtMethod, callSite);
            }


            if (!stack.contains(tgtContextMethod)) {
                traverse(tgtContextMethod);
            }
        }
        stack.pop();
    }


    public Obj getBaseRefObj(Node node) {
        return NodeRepository.getPointObj(node);
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
}
