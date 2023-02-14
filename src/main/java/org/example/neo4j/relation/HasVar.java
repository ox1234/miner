package org.example.neo4j.relation;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.service.RelationExtractor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import soot.SootMethod;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
