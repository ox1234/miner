package org.example.core.basic.identity;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.TypeNode;
import soot.SootMethod;

public class UnifyReturn extends MethodLevelSite implements TypeNode {
    protected String type;

    protected UnifyReturn(SootMethod enclosingMethod, String type) {
        super("return", enclosingMethod.getSignature());
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }
}
