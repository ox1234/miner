package org.example.rulev2;

import org.example.core.basic.node.CallNode;
import soot.SootMethod;

public class TaintThroughRule extends MethodRule {
    public String baseClass;

    public boolean taintThrough(CallNode callNode) {
        SootMethod callee = callNode.getCallee();
        return super.match(callee);
    }
}
