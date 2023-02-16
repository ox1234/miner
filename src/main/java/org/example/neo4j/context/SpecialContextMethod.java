package org.example.neo4j.context;

import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public class SpecialContextMethod extends InstanceContextMethod {
    public SpecialContextMethod(Obj obj, SootMethod sootMethod, Unit callSite) {
        super(obj, sootMethod, callSite);
    }
}
