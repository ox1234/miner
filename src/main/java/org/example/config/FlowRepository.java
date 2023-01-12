package org.example.config;

import org.example.core.basic.Node;

import java.util.*;

public class FlowRepository {
    public static Map<Node, Set<Node>> taintFlowMap = new HashMap<>();

    public static void addTaintFlow(Node to, List<Node> from) {
        if (from == null || to == null || from.isEmpty()) {
            return;
        }

        if (taintFlowMap.containsKey(to)) {
            taintFlowMap.get(to).addAll(from);
        } else {
            Set<Node> nodeSet = new HashSet<>(from);
            taintFlowMap.put(to, nodeSet);
        }
    }
}
