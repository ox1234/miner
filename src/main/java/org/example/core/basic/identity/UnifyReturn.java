package org.example.core.basic.identity;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.TypeNode;
import soot.SootMethod;
import soot.Type;

public class UnifyReturn extends MethodLevelSite implements TypeNode {
    protected Type type;

    protected UnifyReturn(SootMethod enclosingMethod, Type type) {
        super("return", enclosingMethod.getSignature());
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }
}
