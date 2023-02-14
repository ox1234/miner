package org.example.constant;

import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;

public enum InvokeType {
    INSTANCE_INVOKE,
    STATIC_INVOKE,
    SPECIAL_INVOKE;

    public static InvokeType getInvokeType(InvokeExpr invokeExpr) {
        if (invokeExpr instanceof InstanceInvokeExpr) {
            if (invokeExpr instanceof SpecialInvokeExpr) {
                return SPECIAL_INVOKE;
            }
            return INSTANCE_INVOKE;
        } else if (invokeExpr instanceof StaticInvokeExpr) {
            return STATIC_INVOKE;
        }
        return null;
    }
}
