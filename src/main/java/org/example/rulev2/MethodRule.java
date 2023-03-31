package org.example.rulev2;

import soot.SootMethod;

abstract public class MethodRule {
    public String methodFullSig;
    public String methodSubSig;

    public boolean match(SootMethod method) {
        boolean match = false;
        if (methodFullSig != null) {
            match = method.getSignature().equals(methodFullSig);
        }

        if (!match && methodSubSig != null) {
            match = method.getSubSignature().equals(methodSubSig);
        }

        return match;
    }
}
