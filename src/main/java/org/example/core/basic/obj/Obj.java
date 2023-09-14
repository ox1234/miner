package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.UnitLevelSite;
import soot.Type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract public class Obj extends UnitLevelSite implements TypeNode {
    protected Type type;
    protected Map<String, Set<Obj>> fields = new HashMap<>();

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

    public void putField(String fieldID, Set<Obj> objNode) {
        fields.put(fieldID, objNode);
    }

    public Set<Obj> getField(String fieldID) {
        Set<Obj> refObjs = fields.get(fieldID);
        if (refObjs == null) {
            return Collections.emptySet();
        }
        return refObjs;
    }
}
