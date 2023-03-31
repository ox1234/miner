package org.example.flow;

import org.example.core.basic.Global;
import org.example.core.basic.Node;
import org.example.core.basic.obj.Obj;

import java.util.*;

public class PointToContainer {
    private Map<Node, Set<Obj>> pointoNodes = new HashMap<>();
    private static Map<Node, Set<Obj>> globalPointoNodes = new HashMap<>();

    public void addPointRelation(Node node, Set<Obj> objSet) {
        if (objSet.isEmpty()) {
            return;
        }
        if (node instanceof Global) {
            addGlobalPointRelation(node, objSet);
        } else {
            addLocalPointRelation(node, objSet);
        }
    }

    public void addLocalPointRelation(Node node, Set<Obj> objs) {
        pointoNodes.computeIfAbsent(node, k -> new LinkedHashSet<>());
        pointoNodes.get(node).addAll(objs);
    }

    public void addGlobalPointRelation(Node node, Set<Obj> objs) {
        globalPointoNodes.computeIfAbsent(node, k -> new LinkedHashSet<>());
        globalPointoNodes.get(node).addAll(objs);
    }

    public Set<Obj> getPointRefObj(Node node) {
        Set<Obj> obj = pointoNodes.get(node);
        if (obj == null) {
            obj = globalPointoNodes.get(node);
        }
        if (obj == null) {
            obj = Collections.emptySet();
        }
        return obj;
    }
}
