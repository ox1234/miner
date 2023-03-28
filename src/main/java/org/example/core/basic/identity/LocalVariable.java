package org.example.core.basic.identity;

import org.example.core.basic.*;
import soot.SootMethod;

public class LocalVariable extends MethodLevelSite implements TypeNode {
    protected String type;

    protected LocalVariable(String name, SootMethod enclosingMethod, String type) {
        super(name, enclosingMethod.getSignature());
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
