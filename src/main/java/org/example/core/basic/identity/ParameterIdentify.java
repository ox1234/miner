package org.example.core.basic.identity;

import soot.SootMethod;
import soot.Unit;

public class ParameterIdentify extends Identity {
    int idx;

    public ParameterIdentify(String type, int idx, SootMethod enclosingMethod) {
        super(String.format("param-%s-%d", enclosingMethod.getSignature(), idx), type, enclosingMethod, null);
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }
}
