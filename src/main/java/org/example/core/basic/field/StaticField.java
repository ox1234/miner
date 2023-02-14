package org.example.core.basic.field;

import org.example.core.basic.ClassLevelSite;
import org.example.core.basic.TypeNode;
import soot.SootField;

public class StaticField extends ClassLevelSite implements TypeNode {
    private SootField field;

    protected StaticField(SootField field) {
        super(field.getName(), field.getDeclaringClass().getName());
        this.field = field;
    }

    public SootField getFieldRef() {
        return field;
    }

    @Override
    public String getType() {
        return field.getDeclaringClass().getName();
    }
}
