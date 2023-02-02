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
    @Id
    String id;

    @StartNode
    AbstractAllocNode src;

    @EndNode
    AbstractAllocNode tgt;

    public CallRet(AbstractAllocNode src, AbstractAllocNode tgt) {
        this.id = String.format("call-ret:%s:%s", src.id, tgt.id);
        this.src = src;
        this.tgt = tgt;
    }

    public static Set<AbstractRelation> getRelations(Map<Node, Set<CallNode>> callRetMap) {
        Set<AbstractRelation> relations = new HashSet<>();
        callRetMap.forEach((node, callNodes) -> {
            AbstractAllocNode toNode = AbstractAllocNode.getInstance(node);
            for (CallNode callNode : callNodes) {
                AbstractAllocNode fromNode = AbstractAllocNode.getInstance(callNode.getRet());
                relations.add(new CallRet(Objects.requireNonNull(fromNode), Objects.requireNonNull(toNode)));
            }
        });
        return relations;
    }
}
