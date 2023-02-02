package org.example.core.basic.obj;

import soot.SootClass;
import soot.Unit;

public class NormalObj extends Obj {
    private SootClass sootClass;

    protected NormalObj(SootClass sootClass, Unit unit) {
        super(sootClass.getName(), unit);
        this.sootClass = sootClass;
    }
}
