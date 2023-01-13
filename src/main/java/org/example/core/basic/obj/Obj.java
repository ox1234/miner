package org.example.core.basic.obj;

import org.example.core.basic.AbstractNode;
import org.example.util.MethodUtil;
import org.example.util.UnitUtil;
import soot.SootMethod;
import soot.Unit;

abstract public class Obj extends AbstractNode {
    public Obj(String type, SootMethod enclosingMethod, Unit unit) {
        super(type, enclosingMethod, unit);
        super.id = UnitUtil.getObjNodeID(enclosingMethod, unit);
    }
}
