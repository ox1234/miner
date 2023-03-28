package org.example.flow;

import org.example.core.basic.Global;
import org.example.core.basic.Node;
import org.example.core.basic.obj.Obj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PointToContainer {
    private Map<Node, Obj> pointoNodes = new HashMap<>();
    private static Map<Node, Obj> globalPointoNodes = new HashMap<>();

    public void addPointRelation(Node node, Obj obj) {
        if (node instanceof Global) {
            globalPointoNodes.put(node, obj);
        } else {
            pointoNodes.put(node, obj);
        }
    }

    public Obj getPointRefObj(Node node) {
        Obj obj = pointoNodes.get(node);
        if (obj == null) {
            globalPointoNodes.get(node);
        }
        return obj;
    }
}
