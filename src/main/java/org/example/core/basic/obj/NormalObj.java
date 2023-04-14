package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import soot.SootClass;
import soot.Unit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NormalObj extends Obj {
    private Map<Node, ObjField> fieldMap;

    private SootClass sootClass;

    protected NormalObj(SootClass sootClass, Loc loc) {
        super(sootClass.getType(), loc);
        this.sootClass = sootClass;
        this.fieldMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return String.format("%s(normal)", super.getType());
    }

    class ObjField {
        private boolean isTaint;
        private Set<Obj> refObj;

        public ObjField(Set<Obj> refObj) {
            this.refObj = refObj;
        }

        public Set<Obj> getRefObj() {
            return refObj;
        }
    }

    @Override
    public void putField(Node fieldNode, Set<Obj> objNode) {
        fieldMap.put(fieldNode, new ObjField(objNode));
    }

    @Override
    public Set<Obj> getField(Node fieldNode) {
        ObjField objField = fieldMap.get(fieldNode);
        if (objField != null) {
            return objField.getRefObj();
        }
        return Collections.emptySet();
    }

    public Set<Obj> getFieldObjByFieldName(String fieldName) {
        for (Map.Entry<Node, ObjField> kv : fieldMap.entrySet()) {
            Node k = kv.getKey();
            if (k instanceof InstanceField) {
                if (((InstanceField) k).getName().equals(fieldName)) {
                    return kv.getValue().getRefObj();
                }
            }
        }
        return null;
    }
}
