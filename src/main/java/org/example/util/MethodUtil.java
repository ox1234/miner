package org.example.util;

import org.example.config.Global;
import soot.SootMethod;
import soot.tagkit.AnnotationTag;

public class MethodUtil {
    public static boolean isRouteMethod(SootMethod sootMethod) {
        for (AnnotationTag methodTag : ClassUtil.getMethodAnnotation(sootMethod)) {
            if (ClassUtil.isRouteMethodAnnotation(methodTag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSinkMethod(SootMethod sootMethod) {
        return Global.sinks.contains(sootMethod.getSignature());
    }

    public static boolean isLibraryMethod(SootMethod sootMethod) {
        return sootMethod.getDeclaringClass().isLibraryClass();
    }

    public static boolean isPhantomMethod(SootMethod sootMethod) {
        return sootMethod.isPhantom();
    }

    public static boolean isAppMethod(SootMethod sootMethod) {
        return sootMethod.getDeclaringClass().isApplicationClass();
    }
}
