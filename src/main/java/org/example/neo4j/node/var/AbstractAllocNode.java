package org.example.neo4j.node.var;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

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

    public void rebaseID(String objID) {

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
