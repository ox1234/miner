package org.example.core.basic.obj;

import soot.SootClass;
import soot.Unit;

public class PhantomObj extends Obj {
    public PhantomObj(SootClass sootClass) {
        super(sootClass.getName());
    }

    public PhantomObj(SootClass sootClass, Unit unit) {
        super(sootClass.getName(), unit);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", super.getType(), "phantom");
    }
}
