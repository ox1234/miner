package org.example.flow.context;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.Loc;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class InstanceContextMethod extends ContextMethod {
    private Obj obj;

    public InstanceContextMethod(Obj obj, SootMethod sootMethod, CallNode callNode, Loc callSite) {
        super(sootMethod, callNode, callSite);
        this.obj = obj;
    }

    public Obj getObj() {
        return obj;
    }
}
