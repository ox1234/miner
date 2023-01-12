package org.example.core.basic.identity;

import soot.SootMethod;
import soot.Unit;

public class RetIdentity extends Identity {
    public RetIdentity(String name, String type, SootMethod enclosingMethod, Unit unit) {
        super(name, type, enclosingMethod, unit);
    }
}
