package org.example.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.config.Global;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
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

}
