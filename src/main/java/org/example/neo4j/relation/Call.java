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
