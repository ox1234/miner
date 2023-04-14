package org.example.core.basic.obj;

import org.example.core.Loc;
import org.example.core.basic.Node;
import soot.Type;

import java.util.*;

public class ArrayObj extends Obj {
    private Map<Integer, ArrayItem> itemMap;

    protected ArrayObj(Type type, Loc loc) {
        super(type, loc);
        this.itemMap = new HashMap<>();
    }

    class ArrayItem {
        private Set<Obj> refObj;

        public ArrayItem(Set<Obj> objs) {
            refObj = objs;
        }

        public Set<Obj> getRefObj() {
            return refObj;
        }
    }

    @Override
    public void putField(Node fieldNode, Set<Obj> objNode) {
        if (fieldNode instanceof ConstantObj) {
            try {
                int idx = Integer.parseInt(((ConstantObj) fieldNode).getValue(), 10);
                itemMap.put(idx, new ArrayItem(objNode));
            } catch (NumberFormatException ignore) {
            }
        }
    }

    @Override
    public Set<Obj> getField(Node fieldNode) {
        if (fieldNode instanceof ConstantObj) {
            try {
                int idx = Integer.parseInt(((ConstantObj) fieldNode).getValue(), 10);
                ArrayItem arrayItem = itemMap.get(idx);
                if (arrayItem != null) {
                    return arrayItem.getRefObj();
                }
            } catch (NumberFormatException ignore) {
            }
        }
        return Collections.emptySet();
    }
}
