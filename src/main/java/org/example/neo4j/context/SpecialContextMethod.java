package org.example.neo4j.context;

import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public class SpecialContextMethod extends InstanceContextMethod {
    public SpecialContextMethod(Obj obj, SootMethod sootMethod, CallNode callNode, Unit callSite) {
        super(obj, sootMethod, callNode, callSite);
    }
}
