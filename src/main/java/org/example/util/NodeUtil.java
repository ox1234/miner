package org.example.util;

import org.example.core.basic.Node;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeUtil {
    public static Set<String> getNodeID(List<Node> nodeList) {
        if(nodeList == null){
            return Collections.emptySet();
        }
        Set<String> nodeIDs = new HashSet<>();
        for (Node node : nodeList) {
            nodeIDs.add(node.getNodeID());
        }
        return nodeIDs;
    }
}
