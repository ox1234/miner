package org.example.neo4j.context;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public class StaticContextMethod extends ContextMethod {
    private SootClass sootClass;

    public StaticContextMethod(SootClass sootClass, SootMethod sootMethod, Unit callSite) {
        super(sootMethod, callSite);
        this.sootClass = sootClass;
    }
}
