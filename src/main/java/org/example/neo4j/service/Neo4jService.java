package org.example.neo4j.service;

import org.example.config.Global;
import org.example.core.basic.Neo4jNode;
import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayReference;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.node.VoidNode;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.FieldAlloc;
import org.example.neo4j.node.var.ObjAlloc;
import org.example.neo4j.node.var.StaticAlloc;
import org.example.neo4j.relation.*;
import org.example.util.Log;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.util.*;

public class Neo4jService {
    private final SessionFactory sessionFactory;
    Set<AbstractRelation> relations;


    public Neo4jService(Set<AbstractRelation> relations) {
        // 设置neo4j
        Configuration config = new Configuration.Builder().uri(Global.neo4jDSN).credentials(Global.neo4jUser, Global.neo4jPass).build();
        this.sessionFactory = new SessionFactory(config, "org.example.neo4j");
        this.relations = relations;
    }


    public void saveRelation(AbstractRelation relation) {
        Session session = sessionFactory.openSession();
        session.save(relation);
    }

    public void saveRelation(Collection<AbstractRelation> relations) {
        relations.forEach(this::saveRelation);
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
        return allocNode;
    }

    public AbstractAllocNode rebaseThisRef(InstanceField instanceField) {
        Obj obj = instanceField.getBase().getRefObj();
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
}
