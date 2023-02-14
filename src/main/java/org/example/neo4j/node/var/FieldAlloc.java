package org.example.neo4j.node.var;

import soot.SootField;

public class FieldAlloc extends AbstractAllocNode {
    SootField field;

    public FieldAlloc(String objID, SootField field) {
        super(String.format("field:%s:%s", objID, field.getName()), field.getName());
        this.field = field;
    }
}
