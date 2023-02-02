package org.example.neo4j.node.var;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Neo4jNode;
import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.obj.Obj;
import org.example.util.Log;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@NodeEntity
abstract public class AbstractAllocNode {
    String name;

    @Id
    public String id;

    @Relationship(type = "Taint", direction = Relationship.Direction.INCOMING)
    public Set<AbstractAllocNode> taints = new HashSet<>();

    @Relationship(type = "FieldTo", direction = Relationship.Direction.OUTGOING)
    public Set<AbstractAllocNode> fields = new HashSet<>();

    @Relationship(type = "PointTo", direction = Relationship.Direction.OUTGOING)
    public Set<AbstractAllocNode> objs = new HashSet<>();


    protected AbstractAllocNode(String nodeID, String name) {
        this.id = nodeID;
        this.name = name;
    }

    public static AbstractAllocNode getInstance(Node node) {
        if (node instanceof Neo4jNode) {
            return ((Neo4jNode) node).convert();
        }
        Log.error("%s node not support to convert neo4j node", node);
        return null;
    }

    public void addTaint(AbstractAllocNode taintNode) {
        taints.add(taintNode);
    }

    public void addPoint(AbstractAllocNode objNode) {
        objs.add(objNode);
    }

    public void addField(AbstractAllocNode fieldNode) {
        fields.add(fieldNode);
    }
}
