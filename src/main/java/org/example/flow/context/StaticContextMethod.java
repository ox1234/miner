package org.example.flow.context;

import org.example.core.basic.node.CallNode;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

public class StaticContextMethod extends ContextMethod {
    private SootClass sootClass;

    public StaticContextMethod(SootClass sootClass, SootMethod sootMethod, CallNode callNode, Unit callSite) {
        super(sootMethod, callNode, callSite);
        this.sootClass = sootClass;
    }
}
