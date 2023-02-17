package org.example.neo4j.relation;

import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "HASVAR")
public class HasVar extends AbstractRelation {
    @StartNode
    AbstractMethod src;

    @EndNode
    AbstractAllocNode tgt;

    public HasVar(AbstractMethod src, AbstractAllocNode tgt) {
        super(String.format("hasvar:%s:%s", src.getSignature(), tgt.id));
        this.src = src;
        this.tgt = tgt;
    }
}
