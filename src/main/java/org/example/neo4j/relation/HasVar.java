package org.example.neo4j.relation;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.neo4j.node.var.AbstractAllocNode;
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
    @Id
    String id;

    @StartNode
    AbstractMethod src;

    @EndNode
    AbstractAllocNode tgt;

    public HasVar(AbstractMethod src, AbstractAllocNode tgt) {
        this.id = String.format("hasvar:%s:%s", src.getSignature(), tgt.id);
        this.src = src;
        this.tgt = tgt;
    }

    public static Set<AbstractRelation> getRelations(IntraAnalyzedMethod analyzedMethod) {
        Set<AbstractRelation> relations = new HashSet<>();
        AbstractMethod belongMethod = AbstractMethod.getInstance(analyzedMethod.getMethodRef());
        for (MethodLevelSite site : analyzedMethod.getMethodLevelSiteSet()) {
            relations.add(new HasVar(belongMethod, Objects.requireNonNull(AbstractAllocNode.getInstance(site))));
        }
        return relations;
    }

    public static Set<AbstractRelation> getRelations(SootMethod sootMethod) {
        Set<AbstractRelation> relations = new HashSet<>();
        AbstractMethod belongMethod = AbstractMethod.getInstance(sootMethod);
        for (int i = 0; i < sootMethod.getParameterCount(); i++) {
            Node parameter = Site.getNodeInstance(Parameter.class, i, sootMethod, sootMethod.getParameterType(i).toString());
            relations.add(new HasVar(belongMethod, Objects.requireNonNull(AbstractAllocNode.getInstance(parameter))));
        }
        Node unifyRet = Site.getNodeInstance(UnifyReturn.class, sootMethod, sootMethod.getReturnType().toString());
        relations.add(new HasVar(belongMethod, Objects.requireNonNull(AbstractAllocNode.getInstance(unifyRet))));
        return relations;
    }
}
