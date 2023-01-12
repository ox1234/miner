package org.example.core.basic.identity;

import org.example.core.basic.AbstractNode;
import soot.SootMethod;
import soot.Unit;

public class NormalIdentity extends Identity {
    public NormalIdentity(String name, String type, SootMethod enclosingMethod, Unit nodeSite) {
        super(name, type, enclosingMethod, nodeSite);
    }
}
