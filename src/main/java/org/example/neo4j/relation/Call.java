package org.example.neo4j.relation;

import org.example.config.NodeRepository;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.node.CallNode;
import org.example.neo4j.node.method.AbstractMethod;
import org.example.tags.LocationTag;
import org.neo4j.ogm.annotation.*;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RelationshipEntity(type = "CALL")
public class Call extends AbstractRelation {
    @Id
    String callSite;

    List<String> argTypes;

    @StartNode
    AbstractMethod src;

    @EndNode
    AbstractMethod tgt;

    public Call(SootMethod srcMethod, SootMethod targetMethod, Stmt callSite) {
        this.src = AbstractMethod.getInstance(srcMethod);
        this.tgt = AbstractMethod.getInstance(targetMethod);
        this.callSite = LocationTag.getLocation(callSite);
        this.argTypes = this.tgt.getParamTypes();
    }



    public String getCallSite() {
        return callSite;
    }

    public static Set<AbstractRelation> getRelations(SootMethod srcMethod, SootMethod targetMethod, Stmt callSite) {
        Set<AbstractRelation> relations = new HashSet<>();
        relations.add(new Call(srcMethod, targetMethod, callSite));
        return relations;
    }
}
