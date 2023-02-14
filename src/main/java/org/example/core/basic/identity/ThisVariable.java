package org.example.core.basic.identity;

import soot.SootMethod;

public class ThisVariable extends LocalVariable {
    protected ThisVariable(SootMethod enclosingMethod, String type) {
        super("this", enclosingMethod, type);
    }
}
