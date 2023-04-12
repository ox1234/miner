package org.example.flow.context;

import org.example.core.Loc;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.Unit;
import soot.Value;

public class SpecialContextMethod extends InstanceContextMethod {
    public SpecialContextMethod(Obj obj, SootMethod sootMethod, CallNode callNode, Loc callSite) {
        super(obj, sootMethod, callNode, callSite);
    }
}
