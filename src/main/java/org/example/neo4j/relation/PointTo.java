package org.example.neo4j.relation;

import com.sun.istack.NotNull;
import org.example.core.basic.Node;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.ObjAlloc;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

@RelationshipEntity(type = "POINTO")
public class PointTo extends AbstractRelation {
    @StartNode
    AbstractAllocNode src;

    @EndNode
    ObjAlloc tgt;

    public PointTo(@NotNull AbstractAllocNode src, @NotNull ObjAlloc tgt) {
        super(String.format("pointto:%s:%s", src.id, tgt.id));
        this.src = src;
        this.tgt = tgt;
    }
}
