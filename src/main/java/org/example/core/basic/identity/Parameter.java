package org.example.core.basic.identity;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.TypeNode;
import soot.SootMethod;
import soot.Type;

public class Parameter extends MethodLevelSite implements TypeNode {
    protected int idx;
    protected Type type;

    protected Parameter(int idx, SootMethod enclosingMethod, Type type) {
        super(getParameterName(idx), enclosingMethod.getSignature());
        this.idx = idx;
        this.type = type;
    }

    public int getIdx() {
        return idx;
    }

    public Type getSootType() {
        return type;
    }

    @Override
    public Type getType() {
        return type;
    }

    public static String getParameterName(int idx) {
        return String.format("param-%d", idx);
    }
}
