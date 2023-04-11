package org.example.core.basic.obj;

import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.field.InstanceField;
import org.example.tags.LocationTag;
import soot.Type;
import soot.Unit;

import java.util.HashMap;
import java.util.Map;

abstract public class Obj extends UnitLevelSite implements TypeNode {
    private Map<String, ObjField> fieldMap = new HashMap<>();
    protected Type type;

    protected Obj(Type type, Unit unit) {
        super(type.toString(), LocationTag.getLocation(unit));
        this.type = type;
    }

    @Override
    public Type getType() {
        return type;
    }

    public void putInstanceField(InstanceField field, Node value) {
        fieldMap.put(field.getName(), new ObjField(field, value));
    }

    public ObjField getInstanceField(InstanceField field) {
        return fieldMap.get(field.getName());
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
