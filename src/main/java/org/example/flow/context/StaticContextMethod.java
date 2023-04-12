package org.example.flow.context;

import org.example.core.Loc;
import org.example.core.basic.node.CallNode;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class StaticContextMethod extends ContextMethod {
    private SootClass sootClass;

    public StaticContextMethod(SootClass sootClass, SootMethod sootMethod, CallNode callNode, Loc callSite) {
        super(sootMethod, callNode, callSite);
        this.sootClass = sootClass;
    }
}
