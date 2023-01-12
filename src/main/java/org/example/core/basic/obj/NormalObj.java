package org.example.core.basic.obj;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class NormalObj extends Obj {
    SootClass sootClass;

    public NormalObj(SootClass sootClass, SootMethod enclosingMethod, Unit unit) {
        super(sootClass.getName(), enclosingMethod, unit);
        this.sootClass = sootClass;
    }
}
