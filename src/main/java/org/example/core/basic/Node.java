package org.example.core.basic;

import org.example.core.basic.obj.Obj;
import soot.Unit;
import soot.jimple.Stmt;

public interface Node {
    String getID();

    void setRefObj(Obj obj);

    Obj getRefObj();

    void setRefStmt(Stmt unit);

    Stmt getRefStmt();
}
