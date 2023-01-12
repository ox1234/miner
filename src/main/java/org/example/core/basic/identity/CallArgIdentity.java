package org.example.core.basic.identity;

import soot.SootMethod;
import soot.Unit;

public class CallArgIdentity extends Identity {
    public int idx;

    public CallArgIdentity(String name, String type, int idx, SootMethod enclosingMethod, Unit unit) {
        super(name, type, enclosingMethod, unit);
        this.idx = idx;
    }
}
