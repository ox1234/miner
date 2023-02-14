package org.example.neo4j.relation;

import org.example.core.basic.Node;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

@RelationshipEntity
public class CallRet extends AbstractRelation {
    @StartNode
    AbstractAllocNode src;

    @EndNode
    AbstractAllocNode tgt;

    public CallRet(AbstractAllocNode src, AbstractAllocNode tgt) {
        super(String.format("call-ret:%s:%s", src.id, tgt.id));
        this.src = src;
        this.tgt = tgt;
    }
}
