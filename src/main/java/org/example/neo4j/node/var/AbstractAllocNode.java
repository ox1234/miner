package org.example.neo4j.node.var;

import org.example.core.basic.CallNode;
import org.example.core.basic.Node;
import org.example.core.basic.identity.Identity;
import org.example.core.basic.identity.ParameterIdentify;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.relation.Call;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

@NodeEntity
abstract public class AbstractAllocNode {
    @Id
    public String id;

    @Relationship(type = "TAINT", direction = Relationship.Direction.INCOMING)
    public Set<AbstractAllocNode> taints = new HashSet<>();

    @Relationship(type = "POINT", direction = Relationship.Direction.INCOMING)
    public Set<AbstractAllocNode> alias = new HashSet<>();

    protected AbstractAllocNode(Node node) {
        this.id = node.getNodeID();
    }

    public static AbstractAllocNode getInstance(Node node) {
        if (node instanceof Identity) {
            if (node instanceof ParameterIdentify) {
                return new ParamAllocNode((ParameterIdentify) node);
            }
            return new VarAllocNode((Identity) node);
        } else if (node instanceof Obj) {
            return new ObjAllocNode((Obj) node);
        } else if (node instanceof CallNode) {
            return getInstance(((CallNode) node).ret);
        }
        return null;
    }
}
