package org.example.config;

import org.example.core.basic.Node;
import org.example.core.basic.identity.Identity;
import org.example.core.basic.obj.Obj;

import java.util.HashMap;
import java.util.Map;

public class NodeRepository {
    private static Map<String, Node> nodeRepository = new HashMap<>();
    private static Map<String, Identity> identityRepository = new HashMap<>();
    private static Map<String, Obj> objRepository = new HashMap<>();
    private static Map<String, Identity> callNodeRepository = new HashMap<>();

    public static void addNode(Node node) {
        nodeRepository.put(node.getNodeID(), node);
    }

    public static Node getNode(String nodeID) {
        return nodeRepository.get(nodeID);
    }

    public static void addIdentity(Identity node) {
        identityRepository.put(node.getNodeID(), node);
    }

    public static Identity getIdentity(String nodeID) {
        return identityRepository.get(nodeID);
    }

    public static void addObj(Obj obj) {
        objRepository.put(obj.getNodeID(), obj);
    }

    public static Obj getObj(String nodeID) {
        return objRepository.get(nodeID);
    }
}
