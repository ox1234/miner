package org.example.core.basic.field;

import org.example.core.basic.*;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.FieldAlloc;
import org.example.util.Log;
import soot.SootField;
import soot.SootMethod;

public class InstanceField extends MethodLevelSite implements TypeNode {
    protected SootField field;
    protected MethodLevelSite base;
    protected Obj refObj;

    protected InstanceField(MethodLevelSite baseNode, SootField field, SootMethod enclosingMethod) {
        super(String.format("%s.%s", baseNode.getName(), field.getName()), enclosingMethod.getSignature());
        this.base = baseNode;
        this.field = field;
    }

    public Node getBase() {
        return base;
    }

    public SootField getFieldRef() {
        return field;
    }

    public void setRefObj(Obj refObj) {
        this.refObj = refObj;
    }

    @Override
    public String getType() {
        return field.getDeclaringClass().getName();
    }

    @Override
    public String getName() {
        return field.getName();
    }
}
