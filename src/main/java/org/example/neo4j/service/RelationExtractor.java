package org.example.neo4j.service;

import org.example.config.NodeRepository;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Neo4jNode;
import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayReference;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.context.ContextMethod;
import org.example.neo4j.context.InstanceContextMethod;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.FieldAlloc;
import org.example.neo4j.node.var.ObjAlloc;
import org.example.neo4j.node.var.StaticAlloc;
import org.example.neo4j.relation.*;
import org.example.util.Log;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

import java.util.*;

public class RelationExtractor {
    private CallNode callNode;
    private Unit callSite;
    private Set<AbstractRelation> relations = new HashSet<>();
    private IntraAnalyzedMethod analyzedMethod;
    private Stack<ContextMethod> callStack;
    private SootMethod sootMethod;

    private Set<Node> taintNodes;

    public RelationExtractor(CallNode callNode, Unit callSite, SootMethod sootMethod, IntraAnalyzedMethod analyzedMethod, Stack<ContextMethod> callStack, Set<Node> taintNodes) {
        this.callNode = callNode;
        this.callSite = callSite;
        this.analyzedMethod = analyzedMethod;
        this.callStack = callStack;
        this.sootMethod = sootMethod;
        this.taintNodes = taintNodes;
    }

    public AbstractAllocNode getInstance(Node node) {
        if (node instanceof InstanceField) {
            return rebaseThisRef((InstanceField) node);
        } else if (node instanceof ArrayReference) {
            return rebaseArrayRef((ArrayReference) node);
        } else if (node instanceof StaticField) {
            return rebaseStaticRef((StaticField) node);
        } else if (node instanceof Neo4jNode) {
            return ((Neo4jNode) node).convert();
        }
        Log.error("%s node not support to convert neo4j node", node);
        return null;
    }

    public void addMethodRelation() {
        // add method has relation
        Set<MethodLevelSite> methodLocalVariables = NodeRepository.getMethodNodes(sootMethod.getSignature());
        for (MethodLevelSite variable : methodLocalVariables) {
            relations.add(new HasVar(AbstractMethod.getInstance(sootMethod), Objects.requireNonNull(getInstance(variable))));
        }

        // if we analyzed this method, will import body relation
        if (analyzedMethod != null) {
            // add method taint flow
            Map<Node, Set<Node>> taintFlowMap = analyzedMethod.getTaintFlowMap();
            addTaintRelation(taintFlowMap);

            // add method point flow
            Map<Node, Obj> pointFlowMap = analyzedMethod.getPointMap();
            addPointRelation(pointFlowMap);
        }
    }

    public void addRelation() {
        // add target method relation
        addMethodRelation();

        // add arg -> param relation
        if (callNode != null) {
            List<Node> params = callNode.getParams();
            List<Node> args = callNode.getArgs();
            assert params.size() == args.size();
            for (int i = 0; i < params.size(); i++) {
                AbstractAllocNode paramNode = getInstance(params.get(i));
                AbstractAllocNode argNode = getInstance(args.get(i));
                assert paramNode != null && argNode != null;
                relations.add(new Taint(argNode, paramNode));
            }

            // add call return relation
            if (callNode.getRetVar() != null) {
                AbstractAllocNode retNode = getInstance(callNode.getRetVar());
                AbstractAllocNode unifyRetNode = getInstance(callNode.getUnifyRet());
                assert retNode != null && unifyRetNode != null;
                relations.add(new Taint(unifyRetNode, retNode));
            }
        }
    }


    public void addPointRelation(Map<Node, Obj> pointMap) {
        pointMap.forEach((node, obj) -> {
            AbstractAllocNode fromNode = getInstance(node);
            AbstractAllocNode toObj = getInstance(obj);
            relations.add(new PointTo(Objects.requireNonNull(fromNode), (ObjAlloc) Objects.requireNonNull(toObj)));
        });
    }

    public void addTaintRelation(Map<Node, Set<Node>> taintFlowMap) {
        taintFlowMap.forEach((node, nodes) -> {
            AbstractAllocNode toNode = getInstance(node);
            if (containsTaint(nodes)) {
                toNode.setTaint(true);
                taintNodes.add(node);
            }
            for (Node from : nodes) {
                AbstractAllocNode fromNode = getInstance(from);
                relations.add(new Taint(Objects.requireNonNull(fromNode), Objects.requireNonNull(toNode)));
            }
        });
    }

    private boolean containsTaint(Set<Node> originalNodes) {
        for (Node origNode : originalNodes) {
            if (taintNodes.contains(origNode)) {
                return true;
            }
        }
        return false;
    }

    public AbstractAllocNode rebaseThisRef(InstanceField instanceField) {
        Obj obj;
        if (instanceField.getBase() instanceof ThisVariable) {
            InstanceContextMethod methodContext = (InstanceContextMethod) callStack.peek();
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
        AbstractAllocNode idxNode = getInstance(arrayReference.getIdxNode());
        relations.add(new IdxTo(baseNode, idxNode));
        return baseNode;
    }


    public Set<AbstractRelation> getRelations() {
        return relations;
    }
}
