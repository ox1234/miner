package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.TypeNode;
import soot.Type;
import soot.Unit;

public class ConstantObj extends Obj implements TypeNode {
    private String value;

    protected ConstantObj(String constant, Type type, Loc loc) {
        super(type, loc);
        this.value = constant;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", value, super.getType());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ConstantObj)) {
            return false;
        }
        return ((ConstantObj) obj).getValue().equals(this.getValue());
    }
}
