package org.example.neo4j.relation;

import org.example.neo4j.node.method.AbstractMethod;
import org.example.tags.LocationTag;
import org.neo4j.ogm.annotation.*;
import soot.SootMethod;
import soot.Unit;

import java.util.List;

@RelationshipEntity(type = "CALL")
public class Call extends AbstractRelation {
    List<String> argTypes;

    @StartNode
    AbstractMethod src;

    @EndNode
    AbstractMethod tgt;

    public Call(SootMethod srcMethod, SootMethod targetMethod, Unit callSite) {
        super(LocationTag.getLocation(callSite));
        this.src = AbstractMethod.getInstance(srcMethod);
        this.tgt = AbstractMethod.getInstance(targetMethod);
        this.argTypes = this.tgt.getParamTypes();
    }
}
