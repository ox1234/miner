package org.example.core;

import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;

public class Loc {
    private Unit unit;
    private Value expr;
    private int order;

    public Loc(Value expr, int order) {
        this.expr = expr;
        this.order = order;
    }


    public int getOrder() {
        return order;
    }
}
