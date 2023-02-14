package org.example.config;

import org.example.util.TagUtil;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.tagkit.AnnotationTag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class AnnotationRepository {
    private static Map<AnnotationTag, Set<SootClass>> classAnnotationMap = new HashMap<>();
    private static Map<AnnotationTag, Set<SootField>> fieldAnnotationMap = new HashMap<>();
    private static Map<AnnotationTag, Set<SootMethod>> methodAnnotationMap = new HashMap<>();


    public static void addClassAnnotation(AnnotationTag annotation, SootClass sootClass) {
        if (!classAnnotationMap.containsKey(annotation)) {
            classAnnotationMap.put(annotation, new HashSet<>());
        }
        classAnnotationMap.get(annotation).add(sootClass);
    }

    public static void addFieldAnnotation(AnnotationTag annotation, SootField sootField) {
        if (!fieldAnnotationMap.containsKey(annotation)) {
            fieldAnnotationMap.put(annotation, new HashSet<>());
        }
        fieldAnnotationMap.get(annotation).add(sootField);
    }

    public static void addMethodAnnotation(AnnotationTag annotation, SootMethod sootMethod) {
        if (!methodAnnotationMap.containsKey(annotation)) {
            methodAnnotationMap.put(annotation, new HashSet<>());
        }
        methodAnnotationMap.get(annotation).add(sootMethod);
    }

    public static void collectClassAnnotation(SootClass sootClass) {
        TagUtil.getClassAnnotation(sootClass).forEach(annotationTag -> addClassAnnotation(annotationTag, sootClass));
    }

    public static void collectMethodAnnotation(SootMethod sootMethod){
        TagUtil.getMethodAnnotation(sootMethod).forEach(annotationTag -> addMethodAnnotation(annotationTag, sootMethod));
    }

    public static void collectFieldAnnotation(SootField sootField){
        TagUtil.getFieldAnnotation(sootField).forEach(annotationTag -> addFieldAnnotation(annotationTag, sootField));
    }
}
