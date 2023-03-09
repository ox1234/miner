package org.example.flow.context;

import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.Unit;

public class InstanceContextMethod extends ContextMethod {
    private Obj obj;

    public InstanceContextMethod(Obj obj, SootMethod sootMethod, CallNode callNode, Unit callSite) {
        super(sootMethod, callNode, callSite);
        this.obj = obj;
    }

    public Obj getObj() {
        return obj;
    }
}
