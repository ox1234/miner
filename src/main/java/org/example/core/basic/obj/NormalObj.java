package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import soot.SootClass;
import soot.Unit;

import java.util.Map;

public class NormalObj extends Obj {
    private SootClass sootClass;

    protected NormalObj(SootClass sootClass, Loc loc) {
        super(sootClass.getType(), loc);
        this.sootClass = sootClass;
    }

    @Override
    public String toString() {
        return String.format("%s(normal)", super.getType());
    }
}
