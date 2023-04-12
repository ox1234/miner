package org.example.core.basic.field;

import org.example.core.basic.*;
import org.example.core.basic.obj.Obj;
import soot.SootField;
import soot.SootMethod;
import soot.Type;

public class InstanceField extends MethodLevelSite implements TypeNode {
    protected SootField field;
    protected MethodLevelSite base;

    protected InstanceField(MethodLevelSite baseNode, SootField field, SootMethod enclosingMethod) {
        super(String.format("%s.%s", baseNode.getName(), field.getName()), enclosingMethod.getSignature());
        this.base = baseNode;
        this.field = field;
    }

    public Node getBase() {
        return base;
    }

    @Override
    public Type getType() {
        return field.getDeclaringClass().getType();
    }

    @Override
    public String getName() {
        return field.getName();
    }
}
