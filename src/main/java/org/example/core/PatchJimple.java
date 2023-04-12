package org.example.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.util.MethodUtil;
import org.example.util.TagUtil;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PatchJimple {
    private final Logger logger = LogManager.getLogger(PatchJimple.class);
    private Hierarchy hierarchy;
    private SootClass sootClass;

    public PatchJimple(Hierarchy hierarchy, SootClass sootClass) {
        this.hierarchy = hierarchy;
        this.sootClass = sootClass;
    }

    public void patch() {
        sootClass.getFields().forEach(sootField -> {
            // patch resource field
            if (TagUtil.isResourceField(sootField)) {
                logger.info(String.format("%s class patch @Resource field %s", sootClass.getName(), sootField.getName()));
                patchResourceField(sootField);
            }

            // patch autowire field
            if (TagUtil.isAutoWireField(sootField)) {
                logger.info(String.format("%s class patch @Autowire field %s", sootClass.getName(), sootField.getName()));
                patchAutoWireField(sootField);
            }

            // patch value field
            if (TagUtil.isValueField(sootField)) {
                logger.info(String.format("%s class patch @Value field %s", sootClass.getName(), sootField.getName()));
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

    private void patchDependencyInject(SootField sootField, SootClass beanRefClass) {
        // create a $stack = new beanClass();
        SootMethod initMethodRef = getTargetPatchMethod(sootClass.isStatic());
        Body body = initMethodRef.retrieveActiveBody();
        DefaultLocalGenerator localGenerator = new DefaultLocalGenerator(body);
        UnitPatchingChain patchingChain = body.getUnits();

        Local local = localGenerator.generateLocal(beanRefClass.getType());
        List<Unit> addUnits = new ArrayList<>();
        addUnits.add(Jimple.v().newAssignStmt(local, Jimple.v().newNewExpr(beanRefClass.getType())));
        // invoke init method: $stack.special invoke
        addUnits.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(local, MethodUtil.getRefInitMethod(beanRefClass, beanRefClass.isStatic()).makeRef())));
        // assign the field
        addUnits.add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(body.getThisLocal(), sootField.makeRef()), local));
        Collections.reverse(addUnits);
        for (Unit unit : addUnits) {
            patchingChain.addFirst(unit);
        }
    }

    public void patchResourceField(SootField sootField) {
        SootClass beanRefClass = null;
        String beanName = TagUtil.getFieldBeanName(sootField);

        SootClass fieldClass = ((RefType) sootField.getType()).getSootClass();
        List<SootClass> subClasses;
        if (fieldClass.isInterface()) {
            subClasses = hierarchy.getImplementersOf(fieldClass);
        } else {
            subClasses = hierarchy.getSubclassesOfIncluding(fieldClass);
        }
        if (subClasses.size() == 0) {
            return;
        }
        for (SootClass subClass : subClasses) {
            // find service annotation
            if (beanName == null) {
                beanRefClass = subClass;
                break;
            }

            String classBean = TagUtil.getClassBeanName(subClass);
            if (classBean != null && classBean.equals(beanName)) {
                beanRefClass = subClass;
                break;
            }
        }

        if (beanRefClass == null) {
            logger.warn(String.format("get %s class %s field resource bean class fail", sootField.getDeclaringClass().getName(), sootField.getName()));
            return;
        }

        patchDependencyInject(sootField, beanRefClass);

    }


    public SootMethod getTargetPatchMethod(boolean isStatic) {
        SootMethod initMethod = MethodUtil.getRefInitMethod(sootClass, isStatic);
        if (initMethod == null || !initMethod.hasActiveBody()) {
            return null;
        }
        return initMethod;
    }
}

