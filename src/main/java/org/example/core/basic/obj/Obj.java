package org.example.core.basic.obj;

import org.example.core.basic.Neo4jNode;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.field.InstanceField;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.ObjAlloc;
import org.example.tags.LocationTag;
import soot.Unit;

import java.util.HashMap;
import java.util.Map;

abstract public class Obj extends UnitLevelSite implements TypeNode, Neo4jNode {
    private Map<String, TaintField> fieldMap = new HashMap<>();

    class TaintField{
        private boolean isTaint;
        private InstanceField instanceField;

        public TaintField(InstanceField instanceField, boolean isTaint) {
            this.isTaint = isTaint;
            this.instanceField = instanceField;
        }

        public boolean isTaint() {
            return isTaint;
        }

        public void setTaint(boolean taint) {
            isTaint = taint;
        }
    }


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

    @Override
    public AbstractAllocNode convert() {
        return new ObjAlloc(super.getID(), type);
    }

    @Override
    public Obj getRefObj() {
        return this;
    }

    public void putInstanceField(InstanceField field){
        fieldMap.put(field.getName(), new TaintField(field, false));
    }
}
