package org.example.util;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.tagkit.AnnotationTag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MethodUtil {
    private static final String INIT_METHOD_NAME = "<init>";
    private static final String CLINIT_METHOD_NAME = "<clinit>";

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

    public static boolean isMapPutMethod(SootMethod sootMethod) {
        if (ClassUtil.isMapClass(sootMethod.getDeclaringClass())) {
            return sootMethod.getName().equals("put");
        }
        return false;
    }

    public static Set<String> getOverrideMethodSignatureOfInclude(SootMethod sootMethod) {
        Set<String> signatures = new HashSet<>();
        SootClass declaredClass = sootMethod.getDeclaringClass();
        ClassUtil.getAllSuperClasses(declaredClass).forEach(sootClass -> {
            SootMethod superMethod = sootClass.getMethodUnsafe(sootMethod.getSubSignature());
            if (superMethod != null) {
                signatures.add(superMethod.getSignature());
            }
        });
        signatures.add(sootMethod.getSignature());
        return signatures;
    }
}
