package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.field.ArrayLoad;
import org.example.core.basic.field.InstanceField;
import org.example.flow.basic.ArrayField;
import org.example.flow.basic.ObjField;
import soot.Type;

import java.util.HashMap;
import java.util.Map;

abstract public class Obj extends UnitLevelSite implements TypeNode {
    private Map<String, ObjField> fieldMap = new HashMap<>();
    private Map<Obj, ArrayField> arrMap = new HashMap<>();
    protected Type type;

    protected Obj(Type type, Loc loc) {
        super(type.toString(), loc.toString());
        super.setLoc(loc);
        this.type = type;
    }

    protected Obj(Type type, String id) {
        super(type.toString(), id);
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void putInstanceField(InstanceField field, Node value) {
        fieldMap.put(field.getName(), new ObjField(field, value));
    }

    public void putArrayField(ArrayLoad arrLoad, Obj idx, Node value) {
        arrMap.put(idx, new ArrayField(arrLoad, value));
    }

    public ObjField getInstanceField(InstanceField field) {
        return fieldMap.get(field.getName());
    }


    public ArrayField getArrayLoad(Obj idx) {
        return arrMap.get(idx);
    }

    public boolean isTaintField(InstanceField field) {
        return isTaintField(field.getName());
    }

    public void setTaintField(InstanceField field) {
        ObjField objField = getInstanceField(field);
        if (objField == null) {
            objField = new ObjField(field);
            fieldMap.put(field.getName(), objField);
        }
        objField.setTaint(true);
    }

    public boolean isTaintField(String fieldName) {
        ObjField objField = fieldMap.get(fieldName);
        return objField != null && objField.isTaint();
    }
}
