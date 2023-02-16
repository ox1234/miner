package org.example.neo4j.context;

import org.example.core.basic.obj.Obj;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public class InstanceContextMethod extends ContextMethod {
    private Obj obj;

    public InstanceContextMethod(Obj obj, SootMethod sootMethod, Unit callSite) {
        super(sootMethod, callSite);
        this.obj = obj;
    }

    public Obj getObj() {
        return obj;
    }
}
