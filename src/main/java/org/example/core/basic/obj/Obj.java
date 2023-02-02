package org.example.core.basic.obj;

import org.example.core.basic.Neo4jNode;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.ObjAlloc;
import org.example.tags.LocationTag;
import soot.Unit;

abstract public class Obj extends UnitLevelSite implements TypeNode, Neo4jNode {
    protected Obj(String type, Unit unit) {
        super(type, LocationTag.getLocation(unit));
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public AbstractAllocNode convert() {
        return new ObjAlloc(super.getID(), type);
    }
}
