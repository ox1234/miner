package org.example.core.basic.obj;

import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.field.InstanceField;
import org.example.tags.LocationTag;
import soot.Unit;

import java.util.HashMap;
import java.util.Map;

abstract public class Obj extends UnitLevelSite implements TypeNode {
    private Map<String, ObjField> fieldMap = new HashMap<>();

    protected Obj(String type, Unit unit) {
        super(type, LocationTag.getLocation(unit));
    }

    protected Obj(String type) {
        super(type, "fake");
    }

    @Override
    public String getType() {
        return type;
    }

    public void putInstanceField(InstanceField field, Node value) {
        fieldMap.put(field.getName(), new ObjField(field, value));
    }

    public ObjField getInstanceField(InstanceField field) {
        return fieldMap.get(field.getName());
    }

    public boolean isTaintField(InstanceField field) {
        ObjField objField = fieldMap.get(field.getName());
        return objField != null && objField.isTaint();
    }

    public void setTaintField(InstanceField field) {
        ObjField objField = getInstanceField(field);
        if (objField == null) {
            objField = new ObjField(field);
            fieldMap.put(field.getName(), objField);
        }
        objField.setTaint(true);
    }
}
