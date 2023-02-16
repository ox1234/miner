package org.example.core;

import org.example.util.Log;
import org.example.util.TagUtil;
import soot.*;
import soot.javaToJimple.DefaultLocalGenerator;
import soot.jimple.Jimple;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class PatchJimple {
    private Hierarchy hierarchy;
    private SootClass sootClass;

    private static final String INIT_METHOD_NAME = "<init>";

    public PatchJimple(Hierarchy hierarchy, SootClass sootClass) {
        this.hierarchy = Scene.v().getActiveHierarchy();
        this.sootClass = sootClass;
    }

    public void patch() {
        sootClass.getFields().forEach(new Consumer<SootField>() {
            @Override
            public void accept(SootField sootField) {
                if (TagUtil.isResourceField(sootField)) {
                    patchResourceField(sootField);
                }
            }
        });
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

        SootMethod initMethodRef = getInitMethod(sootClass);
        if (initMethodRef == null || initMethodRef.hasActiveBody()) {
            return;
        }

        Body body = initMethodRef.retrieveActiveBody();
        DefaultLocalGenerator localGenerator = new DefaultLocalGenerator(body);
        UnitPatchingChain patchingChain = body.getUnits();
        // create a $stack = new beanClass();
        Local local = localGenerator.generateLocal(beanRefClass.getType());
        patchingChain.add(Jimple.v().newAssignStmt(local, Jimple.v().newNewExpr(beanRefClass.getType())));
        // invoke init method: $stack.special invoke
        patchingChain.add(Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(local, initMethodRef.makeRef())));
        // assign the field
        patchingChain.add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(body.getThisLocal(), sootField.makeRef()), local));
    }

    private SootMethod getInitMethod(SootClass sootClass) {
        return sootClass.getMethod(INIT_METHOD_NAME, Collections.emptyList());
    }
}

