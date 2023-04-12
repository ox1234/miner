package org.example.util;

import org.example.soot.SourceType;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;

public class ClassUtil {
    public static SourceType getClassSourceType(SootClass sootClass) {
        if (sootClass.isApplicationClass()) {
            return SourceType.APP_CLASS;
        } else if (sootClass.isLibraryClass()) {
            return SourceType.LIB_CLASS;
        } else if (sootClass.isPhantomClass()) {
            return SourceType.PHANTOM_CLASS;
        } else {
            return SourceType.UNKNOWN_CLASS;
        }
    }

    public static void setClassType(SootClass sootClass, SourceType sourceType) {
        switch (sourceType) {
            case APP_CLASS:
                sootClass.setApplicationClass();
                break;
            case PHANTOM_CLASS:
                sootClass.setPhantomClass();
                break;
            case LIB_CLASS:
                sootClass.setLibraryClass();
                break;
        }
    }

    public static boolean isMapClass(SootClass testClass) {
        return isTargetClassSubClass(testClass, "java.util.Map");
    }


    public static boolean isTargetClassSubClass(SootClass testClass, SootClass superClass) {
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        if (superClass.isInterface()) {
            return hierarchy.getImplementersOf(superClass).contains(testClass);
        } else {
            return hierarchy.getSubclassesOfIncluding(superClass).contains(testClass);
        }
    }

    public static boolean isTargetClassSubClass(SootClass testClass, String superClassName) {
        SootClass superClass = Scene.v().loadClassAndSupport(superClassName);
        if (superClass != null) {
            return isTargetClassSubClass(testClass, superClass);
        }
        return false;
    }
}
