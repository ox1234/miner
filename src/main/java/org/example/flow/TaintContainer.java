package org.example.flow;

import org.example.core.basic.Global;
import org.example.core.basic.Node;
import org.example.core.basic.field.StaticField;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TaintContainer {
    private Set<Node> taintNodes = new LinkedHashSet<>();
    private static Set<Node> globalTaintNodes = new LinkedHashSet<>();

    public void addTaint(Node node) {
        if (node instanceof Global) {
            globalTaintNodes.add(node);
        } else {
            taintNodes.add(node);
        }
    }

    public boolean containsTaint(Node node) {
        return taintNodes.contains(node) || globalTaintNodes.contains(node);
    }

    public static void addGlobalTaint(Node node) {
        globalTaintNodes.add(node);
    }
}
