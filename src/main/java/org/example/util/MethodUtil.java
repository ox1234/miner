package org.example.util;

import org.example.config.Global;
import soot.Scene;
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

    public static SootMethod getSootMethod(String signature) {
        return Scene.v().getMethod(signature);
    }

    public static SootMethod getRefInitMethod(SootClass sootClass, boolean isStatic) {
        if (isStatic) {
            return sootClass.getMethodUnsafe(CLINIT_METHOD_NAME, Collections.emptyList(), VoidType.v());
        } else {
            return sootClass.getMethodUnsafe(INIT_METHOD_NAME, Collections.emptyList(), VoidType.v());
        }
    }
}
