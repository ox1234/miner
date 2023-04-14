package org.example.util;

import org.example.core.basic.Node;
import org.example.core.basic.obj.Obj;

import java.util.LinkedHashSet;
import java.util.Set;

public class NodeUtil {
    public static Set<Node> convertToNodeSet(Set<Obj> objs){
        return new LinkedHashSet<>(objs);
    }
}
