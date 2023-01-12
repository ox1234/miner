package org.example.core.basic.identity;

import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class StaticFieldIdentity extends Identity {
    public StaticFieldIdentity(SootField field, SootMethod enclosingMethod, Unit nodeSite) {
        super(String.format("sfield-%s.%s", field.getDeclaringClass().getName(), field.getName()), field.getDeclaringClass().getName(), enclosingMethod, nodeSite);
    }
}
