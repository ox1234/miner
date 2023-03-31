package org.example.util;

import org.example.config.Global;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.tagkit.AnnotationTag;

import java.util.Collections;

public class MethodUtil {
    private static final String INIT_METHOD_NAME = "<init>";
    private static final String CLINIT_METHOD_NAME = "<clinit>";

    public static boolean isRouteMethod(SootMethod sootMethod) {
        for (AnnotationTag methodTag : TagUtil.getMethodAnnotation(sootMethod)) {
            if (TagUtil.isRouteMethodAnnotation(methodTag)) {
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

    public static SootMethod getRefInitMethod(SootClass sootClass, boolean isStatic) {
        if (isStatic) {
            return sootClass.getMethodUnsafe(CLINIT_METHOD_NAME, Collections.emptyList(), VoidType.v());
        } else {
            return sootClass.getMethodUnsafe(INIT_METHOD_NAME, Collections.emptyList(), VoidType.v());
        }
    }
}
