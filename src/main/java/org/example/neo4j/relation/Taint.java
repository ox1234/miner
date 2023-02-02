package org.example.neo4j.relation;

import org.example.core.basic.Node;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RelationshipEntity(type = "TAINT")
public class Taint extends AbstractRelation {
    @Id
    String id;

    @StartNode
    AbstractAllocNode src;

    @EndNode
    AbstractAllocNode tgt;

    public Taint(AbstractAllocNode src, AbstractAllocNode tgt) {
        this.id = String.format("taint:%s:%s", src.id, tgt.id);
        this.src = src;
        this.tgt = tgt;
    }

    public static Set<AbstractRelation> getRelations(Map<Node, Set<Node>> taintFlowMap) {
        Set<AbstractRelation> relations = new HashSet<>();
        taintFlowMap.forEach((node, nodes) -> {
            AbstractAllocNode toNode = AbstractAllocNode.getInstance(node);
            for (Node from : nodes) {
                AbstractAllocNode fromNode = AbstractAllocNode.getInstance(from);
                relations.add(new Taint(Objects.requireNonNull(fromNode), Objects.requireNonNull(toNode)));
            }
        });
        return relations;
    }
}
