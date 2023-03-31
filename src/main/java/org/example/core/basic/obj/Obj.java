package org.example.core.basic.obj;

import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.field.InstanceField;
import org.example.tags.LocationTag;
import org.example.util.Log;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.Unit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

abstract public class Obj extends UnitLevelSite implements TypeNode {
    private Map<String, ObjField> fieldMap = new HashMap<>();

    protected Obj(String type, Unit unit) {
        super(type, LocationTag.getLocation(unit));
    }

    protected Obj(String type, String id) {
        super(type, "fake-" + id);
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

    public static Obj getPhantomObj(String type) {
        return new PhantomObj(type);
    }

    public static Obj getPhantomObj(Type type, String id) {
        return getPhantomObj(type.toString(), id);
    }

    public static Obj getPhantomObj(String type, String id) {
        SootClass sootClass = Scene.v().getSootClass(type);
        Obj obj = new PhantomObj(sootClass, id);
        if (sootClass.isApplicationClass()) {
            Log.error("%s node can't find referenced object, will create a phantom obj", type);
        }
        return obj;
    }
}
