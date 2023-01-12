package org.example.core.basic.obj;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.*;

public class ConstantObj extends Obj {
    private String value;

    public ConstantObj(StringConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = constant.value;
    }

    public ConstantObj(LongConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = String.valueOf(constant.value);
    }

    public ConstantObj(IntConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = String.valueOf(constant.value);
    }

    public ConstantObj(DoubleConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = String.valueOf(constant.value);
    }

    public ConstantObj(FloatConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = String.valueOf(constant.value);
    }

    public ConstantObj(ClassConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = constant.value;
    }

    public ConstantObj(NullConstant constant, SootMethod enclosingMethod, Unit unit) {
        super(constant.getType().toString(), enclosingMethod, unit);
        this.value = "null";
    }

    public String getValue() {
        return value;
    }
}
