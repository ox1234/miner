package org.example.util;

import org.example.config.Global;
import org.example.constant.SpringAnnotation;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TagUtil {
    public static boolean isSpringControllerAnnotation(AnnotationTag tag) {
        return Global.rule.filter.controllerClassTags.contains(tag.getType());
    }

    public static boolean isRouteMethodAnnotation(AnnotationTag tag) {
        return Global.rule.filter.requestMethodTags.contains(tag.getType());
    }

    public static List<String> getMethodRoutePath(SootMethod sootMethod) {
        List<String> routeList = new ArrayList<>();
        for (String routeAnnotation : Global.rule.filter.requestMethodTags) {
            AnnotationTag annotationTag = searchAnnotation(getMethodAnnotation(sootMethod), routeAnnotation);
            if (annotationTag == null) {
                continue;
            }
            AnnotationElem annotationElem = getAnnotationElem(annotationTag, "value");
            if (annotationElem instanceof AnnotationArrayElem) {
                ((AnnotationArrayElem) annotationElem).getValues().forEach(annotationElem1 -> {
                    if (annotationElem1 instanceof AnnotationStringElem) {
                        routeList.add(((AnnotationStringElem) annotationElem1).getValue());
                    }
                });
            }
            return routeList;
        }
        return routeList;
    }

    public static boolean isServiceClass(SootClass sootClass) {
        return checkAnnotation(getClassAnnotation(sootClass), SpringAnnotation.serviceAnnotation);
    }

    public static boolean isComponentClass(SootClass sootClass) {
        return checkAnnotation(getClassAnnotation(sootClass), SpringAnnotation.componentAnnotation);
    }

    public static boolean isMyBatisWrapper(SootClass sootClass) {
        return checkAnnotation(getClassAnnotation(sootClass), SpringAnnotation.mybatisMapperAnnotation);
    }

    public static boolean isAutoWireField(SootField sootField) {
        return checkAnnotation(getFieldAnnotation(sootField), SpringAnnotation.autoWireAnnotation);
    }

    public static boolean isValueField(SootField sootField) {
        return checkAnnotation(getFieldAnnotation(sootField), SpringAnnotation.valueAnnotation);
    }

    public static boolean isResourceField(SootField sootField) {
        return checkAnnotation(getFieldAnnotation(sootField), SpringAnnotation.resourceAnnotation);
    }

    public static AnnotationTag getValueAnnotation(SootField sootField) {
        return searchAnnotation(getFieldAnnotation(sootField), SpringAnnotation.valueAnnotation);
    }

    public static String getValueAnnotationValue(SootField sootField) {
        AnnotationTag annotationTag = getValueAnnotation(sootField);
        AnnotationElem annotationElem = getAnnotationElem(annotationTag, "value");
        if (annotationElem instanceof AnnotationStringElem) {
            return ((AnnotationStringElem) annotationElem).getValue();
        }
        return "";
    }

    public static AnnotationTag getServiceAnnotation(SootClass sootClass) {
        return searchAnnotation(getClassAnnotation(sootClass), SpringAnnotation.serviceAnnotation);
    }

    public static AnnotationTag getComponentAnnotation(SootClass sootClass) {
        return searchAnnotation(getClassAnnotation(sootClass), SpringAnnotation.componentAnnotation);
    }

    public static AnnotationTag getMyBatisAnnotation(SootClass sootClass) {
        return searchAnnotation(getClassAnnotation(sootClass), SpringAnnotation.mybatisMapperAnnotation);
    }

    public static AnnotationTag getAutoWireAnnotation(SootField sootField) {
        return searchAnnotation(getFieldAnnotation(sootField), SpringAnnotation.autoWireAnnotation);
    }

    public static AnnotationTag getResourceAnnotation(SootField sootField) {
        return searchAnnotation(getFieldAnnotation(sootField), SpringAnnotation.resourceAnnotation);
    }

    public static AnnotationElem getAnnotationElem(AnnotationTag annotationTag, String name) {
        for (AnnotationElem elem : annotationTag.getElems()) {
            if (elem.getName().equals(name)) {
                return elem;
            }
        }
        return null;
    }

    public static String getClassBeanName(SootClass sootClass) {
        AnnotationTag annotationTag = null;
        if (isServiceClass(sootClass)) {
            annotationTag = getServiceAnnotation(sootClass);
        } else if (isComponentClass(sootClass)) {
            annotationTag = getComponentAnnotation(sootClass);
        }

        if (annotationTag != null) {
            AnnotationElem elem = getAnnotationElem(annotationTag, "name");
            if (elem instanceof AnnotationStringElem) {
                return ((AnnotationStringElem) elem).getValue();
            }
        }
        return null;
    }

    public static String getFieldBeanName(SootField sootField) {
        AnnotationTag annotationTag = getResourceAnnotation(sootField);
        if (annotationTag != null) {
            AnnotationElem elem = getAnnotationElem(annotationTag, "name");
            if (elem instanceof AnnotationStringElem) {
                return ((AnnotationStringElem) elem).getValue();
            }
        }
        return null;
    }


    private static boolean checkAnnotation(List<AnnotationTag> annotationTags, String annotationType) {
        return searchAnnotation(annotationTags, annotationType) != null;
    }

    private static AnnotationTag searchAnnotation(List<AnnotationTag> annotationTags, String annotationType) {
        for (AnnotationTag annotationTag : annotationTags) {
            if (annotationTag.getType().equals(annotationType)) {
                return annotationTag;
            }
        }
        return null;
    }


//    public static boolean isResourceField(SootField sootField){
//        for (AnnotationTag annotationTag : getFieldAnnotation(sootField)) {
//            if(annotationTag.getType().equals(SpringAnnotation.))
//        }
//    }


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
