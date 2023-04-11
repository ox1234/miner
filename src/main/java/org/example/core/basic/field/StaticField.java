package org.example.core.basic.field;

import org.example.core.basic.ClassLevelSite;
import org.example.core.basic.Global;
import org.example.core.basic.TypeNode;
import soot.SootField;
import soot.Type;

public class StaticField extends ClassLevelSite implements TypeNode, Global {
    private SootField field;

    protected StaticField(SootField field) {
        super(field.getName(), field.getDeclaringClass().getType().toString());
        this.field = field;
    }

    public SootField getFieldRef() {
        return field;
    }

    @Override
    public Type getType() {
        return field.getDeclaringClass().getType();
    }
}
