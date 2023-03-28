package org.example.core.basic.obj;

import org.example.core.basic.TypeNode;
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
    public String toString() {
        return String.format("%s(%s)", value, super.getType());
    }
}
