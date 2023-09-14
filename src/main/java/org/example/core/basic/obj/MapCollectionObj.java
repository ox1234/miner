package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import soot.SootClass;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapCollectionObj extends NormalObj implements CollectionObj {
    private Map<Obj, Set<Obj>> kvSet = new HashMap<>();

    protected MapCollectionObj(SootClass type, Loc loc) {
        super(type, loc);
    }

    public void putKV(Obj key, Set<Obj> val) {
        kvSet.put(key, val);
    }

    public Set<Obj> getKV(Obj key) {
        return kvSet.get(key) == null ? Collections.emptySet() : kvSet.get(key);
    }
}
