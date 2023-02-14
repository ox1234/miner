package org.example.util;

import org.example.soot.SourceType;
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

}
