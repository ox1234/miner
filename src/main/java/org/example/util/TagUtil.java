package org.example.util;

import org.example.config.Global;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;

import java.util.Collections;
import java.util.List;

public class TagUtil {
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

    public static List<AnnotationTag> getFieldAnnotation(SootField sootField) {
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootField.getTag("VisibilityAnnotationTag");
        if (annotationTag == null) {
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }
}
