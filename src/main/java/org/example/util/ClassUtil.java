package org.example.util;

import org.example.config.Global;
import org.example.soot.SourceType;
import soot.SootClass;
import soot.SootMethod;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;

import java.util.Collections;
import java.util.List;

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

    public static boolean isSpringControllerAnnotation(AnnotationTag tag) {
        return Global.rule.filter.controllerClassTags.contains(tag.getType());
    }

    public static boolean isRouteMethodAnnotation(AnnotationTag tag) {
        return Global.rule.filter.requestMethodTags.contains(tag.getType());
    }

    public static List<AnnotationTag> getClassAnnotation(SootClass sootClass) {
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootClass.getTag("VisibilityAnnotationTag");
        if (annotationTag == null) {
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }

    public static List<AnnotationTag> getMethodAnnotation(SootMethod sootMethod) {
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootMethod.getTag("VisibilityAnnotationTag");
        if (annotationTag == null) {
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }
}
