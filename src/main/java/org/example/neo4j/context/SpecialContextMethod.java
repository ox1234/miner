package org.example.neo4j.context;

import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.jimple.Stmt;

public class SpecialContextMethod extends InstanceContextMethod {
    public SpecialContextMethod(Obj obj, SootMethod sootMethod, Stmt callSite) {
        super(obj, sootMethod, callSite);
    }
}
