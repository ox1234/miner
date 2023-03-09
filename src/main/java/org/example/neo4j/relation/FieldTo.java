package org.example.neo4j.relation;

import org.example.neo4j.node.var.AbstractAllocNode;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity("FIELDTO")
public class FieldTo extends AbstractRelation {
    @StartNode
    AbstractAllocNode src;

    @EndNode
    AbstractAllocNode tgt;

    public FieldTo(AbstractAllocNode src, AbstractAllocNode tgt) {
        super(String.format("field:%s:%s", src.id, tgt.id));
        this.src = src;
        this.tgt = tgt;
    }
}
