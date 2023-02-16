package org.example.core;

import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class CGBuilder {
    private List<SootMethod> entryMethods;
    private Hierarchy hierarchy;
    private Queue<SootMethod> queue;

    public CGBuilder(List<SootMethod> entryMethods) {
        this.entryMethods = entryMethods;
        this.hierarchy = Scene.v().getActiveHierarchy();
    }

    private SootMethod dispatch(Obj obj, String methodSubSig) {
        SootClass sootClass = Scene.v().loadClassAndSupport(obj.getType());
        if (obj instanceof PhantomObj) {
            hierarchy.getSuperclassesOfIncluding(sootClass).forEach(new Consumer<SootClass>() {
                @Override
                public void accept(SootClass sootClass) {

                }
            });
            return sootClass.getMethod(methodSubSig);
        }
        return null;
    }
}
