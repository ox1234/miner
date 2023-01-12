package org.example.core.basic.identity;

import soot.SootMethod;
import soot.Unit;

public class RetNodeIdentity extends Identity {
    public RetNodeIdentity(SootMethod enclosingMethod, Unit nodeSite) {
        super(String.format("ret-%s", enclosingMethod.getSignature()), enclosingMethod.getReturnType().toString(), enclosingMethod, nodeSite);
    }
}
