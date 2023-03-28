package org.example.core.basic.identity;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.TypeNode;
import soot.SootMethod;

public class Parameter extends MethodLevelSite implements TypeNode {
    protected int idx;
    protected String type;

    protected Parameter(int idx, SootMethod enclosingMethod, String type) {
        super(getParameterName(idx), enclosingMethod.getSignature());
        this.idx = idx;
        this.type = type;
    }

    public int getIdx() {
        return idx;
    }

    @Override
    public String getType() {
        return type;
    }

    public static String getParameterName(int idx) {
        return String.format("param-%d", idx);
    }
}
