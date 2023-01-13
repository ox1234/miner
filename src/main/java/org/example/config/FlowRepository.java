package org.example.config;

import org.example.core.basic.Node;

import java.util.*;

public class FlowRepository {
    public static Map<String, Set<String>> taintFlowMap = new HashMap<>();

    public static void addTaintFlow(String to, Set<String> from) {
        if (from == null || to == null || from.isEmpty()) {
            return;
        }

        if (taintFlowMap.containsKey(to)) {
            taintFlowMap.get(to).addAll(from);
        } else {
            Set<String> nodeSet = new HashSet<>(from);
            taintFlowMap.put(to, nodeSet);
        }
    }

    public static Set<String> getTaintFlow(String to) {
        Set<String> fromNodes = taintFlowMap.get(to);
        if (fromNodes == null) {
            return Collections.emptySet();
        }
        return fromNodes;
    }
}
