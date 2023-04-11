package org.example.core.basic.obj;

import soot.SootClass;
import soot.Unit;

public class PhantomObj extends Obj {
    public PhantomObj(SootClass sootClass, Unit location) {
        super(sootClass.getType(), location);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", super.getType(), "phantom");
    }
}
