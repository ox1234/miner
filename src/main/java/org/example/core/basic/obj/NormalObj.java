package org.example.core.basic.obj;

import org.example.core.basic.Node;
import soot.SootClass;
import soot.Unit;

import java.util.Map;

public class NormalObj extends Obj {
    private SootClass sootClass;

    protected NormalObj(SootClass sootClass, Unit unit) {
        super(sootClass.getName(), unit);
        this.sootClass = sootClass;
    }

    public NormalObj(SootClass sootClass) {
        super(sootClass.getName());
        this.sootClass = sootClass;
    }

    @Override
    public String toString() {
        return String.format("%s(normal)", super.getType());
    }
}
