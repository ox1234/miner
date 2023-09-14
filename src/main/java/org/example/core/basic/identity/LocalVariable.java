package org.example.core.basic.identity;

import org.example.core.basic.*;
import soot.SootMethod;
import soot.Type;

public class LocalVariable extends MethodLevelSite implements TypeNode {
    protected Type type;

    protected LocalVariable(String name, SootMethod enclosingMethod, Type type) {
        super(name, enclosingMethod.getSignature());
        this.type = type;
    }

    public Type getSootType() {
        return type;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void resetType(Type type) {
        this.type = type;
    }
}
