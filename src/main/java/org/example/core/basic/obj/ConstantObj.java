package org.example.core.basic.obj;

import org.example.core.basic.TypeNode;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.ObjAlloc;
import soot.Unit;

public class ConstantObj extends Obj implements TypeNode {
    private String value;

    protected ConstantObj(String constant, String type, Unit unit) {
        super(constant, unit);
        this.value = constant;
        super.type = type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public AbstractAllocNode convert() {
        return new ObjAlloc(super.getID(), String.format("%s:%s", value, type));
    }
}
