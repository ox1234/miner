package org.example.core;

import org.example.util.Log;
import org.example.util.MethodUtil;
import org.example.util.TagUtil;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PatchJimple {
    private Hierarchy hierarchy;
    private SootClass sootClass;

    public PatchJimple(Hierarchy hierarchy, SootClass sootClass) {
        this.hierarchy = Scene.v().getActiveHierarchy();
        this.sootClass = sootClass;
    }

    public void patch() {
        sootClass.getFields().forEach(sootField -> {
            // patch resource field
            if (TagUtil.isResourceField(sootField)) {
                patchResourceField(sootField);
            }

            // patch autowire field
            if (TagUtil.isAutoWireField(sootField)) {
                patchAutoWireField(sootField);
            }

            // patch value field
            if (TagUtil.isValueField(sootField)) {
                patchValueField(sootField);
            }
        });
    }

    public void patchAutoWireField(SootField sootField) {
        SootMethod initMethodRef = getTargetPatchMethod(sootClass.isStatic());
        Body body = initMethodRef.retrieveActiveBody();
        UnitPatchingChain patchingChain = body.getUnits();
        DefaultLocalGenerator localGenerator = new DefaultLocalGenerator(body);

        Type fieldType = sootField.getType();
        if (fieldType instanceof RefType) {
            List<Unit> addUnits = new ArrayList<>();
            Local local = localGenerator.generateLocal(fieldType);

            addUnits.add(Jimple.v().newAssignStmt(local, Jimple.v().newNewExpr((RefType) fieldType)));
            addUnits.add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(body.getThisLocal(), sootField.makeRef()), local));

            Collections.reverse(addUnits);
            for (Unit unit : addUnits) {
                patchingChain.addFirst(unit);
            }
        }
    }

    public void patchValueField(SootField sootField) {
        SootMethod initMethodRef = getTargetPatchMethod(sootClass.isStatic());
        Body body = initMethodRef.retrieveActiveBody();
        UnitPatchingChain patchingChain = body.getUnits();

        StringConstant stringConstant = StringConstant.v(TagUtil.getValueAnnotationValue(sootField));

        List<Unit> addUnits = new ArrayList<>();
        addUnits.add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(body.getThisLocal(), sootField.makeRef()), stringConstant));

        Collections.reverse(addUnits);
        for (Unit unit : addUnits) {
            patchingChain.addFirst(unit);
        }
    }

    public void patchResourceField(SootField sootField) {
        SootClass beanRefClass = null;
        String beanName = TagUtil.getFieldBeanName(sootField);

        List<SootClass> subClasses = hierarchy.getSubclassesOf(sootField.getDeclaringClass());
        if (subClasses.size() == 0) {
            return;
        }
        for (SootClass subClass : subClasses) {
            String classBean = TagUtil.getClassBeanName(subClass);
            if (classBean != null && classBean.equals(beanName)) {
                beanRefClass = subClass;
                break;
            }
            // find service annotation
            if (beanName == null) {
                beanRefClass = subClass;
            }
        }

        if (beanRefClass == null) {
            Log.warn("get %s class %s field resource bean class fail", sootField.getDeclaringClass().getName(), sootField.getName());
            return;
        }

        // create a $stack = new beanClass();
        SootMethod initMethodRef = getTargetPatchMethod(sootClass.isStatic());
        Body body = initMethodRef.retrieveActiveBody();
        DefaultLocalGenerator localGenerator = new DefaultLocalGenerator(body);
        UnitPatchingChain patchingChain = body.getUnits();

        Local local = localGenerator.generateLocal(beanRefClass.getType());
        List<Unit> addUnits = new ArrayList<>();
        addUnits.add(Jimple.v().newAssignStmt(local, Jimple.v().newNewExpr(beanRefClass.getType())));
        // invoke init method: $stack.special invoke
        addUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(local, initMethodRef.makeRef())));
        // assign the field
        addUnits.add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(body.getThisLocal(), sootField.makeRef()), local));

        Collections.reverse(addUnits);
        patchingChain.addAll(addUnits);
    }


    public SootMethod getTargetPatchMethod(boolean isStatic) {
        SootMethod initMethod = MethodUtil.getRefInitMethod(sootClass, isStatic);
        if (initMethod == null || !initMethod.hasActiveBody()) {
            return null;
        }
        return initMethod;
    }
}

