package org.example.core.basic.identity;

import soot.SootMethod;
import soot.Type;

public class ThisVariable extends LocalVariable {
    protected ThisVariable(SootMethod enclosingMethod, Type type) {
        super("this", enclosingMethod, type);
    }
}
