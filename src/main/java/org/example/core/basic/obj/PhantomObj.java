package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import soot.SootClass;
import soot.Type;
import soot.Unit;

public class PhantomObj extends Obj {
    public PhantomObj(Type type, Node orignalNode) {
        super(type, orignalNode.getID());
    }


    @Override
    public String toString() {
        return String.format("%s(%s)", super.getType(), "phantom");
    }

    @Override
    public void resetType(Type type) {
        super.type = type;
    }
}
