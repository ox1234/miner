package org.example.config;

import org.example.core.basic.*;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.relation.Call;
import soot.jimple.ThisRef;

import java.util.*;

// NodeRepository Node仓库，指针分析引擎会将所有的变量抽象为Node节点，此类存放在程序中所有的Node抽象
public class NodeRepository {
    private static Map<String, Node> nodeRepository = new HashMap<>();

    private static Map<String, MethodLevelSite> methodLevelSiteMap = new HashMap<>();
    private static Map<String, ClassLevelSite> classLevelSiteMap = new HashMap<>();
    private static Map<String, UnitLevelSite> unitLevelSiteMap = new HashMap<>();
    private static Map<String, SiteLevelSite> siteLevelSiteMap = new HashMap<>();

    private static Map<String, Set<MethodLevelSite>> methodNodesMap = new HashMap<>();
    private static Map<String, List<CallNode>> methodCalleeMap = new HashMap<>();

    private static Map<Node, Obj> pointToObj = new HashMap<>();

    public static void addNode(Node node) {
        if (node instanceof MethodLevelSite) {
            MethodLevelSite methodNode = (MethodLevelSite) node;
            methodLevelSiteMap.put(node.getID(), methodNode);
            if (!ignoredMethodNode(node)) {
                if (!methodNodesMap.containsKey(methodNode.getMethodSig())) {
                    methodNodesMap.put(methodNode.getMethodSig(), new HashSet<>());
                }
                methodNodesMap.get(methodNode.getMethodSig()).add(methodNode);
            }
        } else if (node instanceof ClassLevelSite) {
            classLevelSiteMap.put(node.getID(), (ClassLevelSite) node);
        } else if (node instanceof UnitLevelSite) {
            if (node instanceof CallNode) {
                CallNode callNode = (CallNode) node;
                if (!methodCalleeMap.containsKey(callNode.getEnclosingMethodSig())) {
                    methodCalleeMap.put(callNode.getEnclosingMethodSig(), new ArrayList<>());
                }
                methodCalleeMap.get(callNode.getEnclosingMethodSig()).add(callNode);
            }
            unitLevelSiteMap.put(node.getID(), (UnitLevelSite) node);
        } else if (node instanceof SiteLevelSite) {
            siteLevelSiteMap.put(node.getID(), (SiteLevelSite) node);
        }

        nodeRepository.put(node.getID(), node);
    }

    public static Node getNode(String nodeID) {
        return nodeRepository.get(nodeID);
    }

    public static void addPointTo(Node node, Obj obj) {
        pointToObj.put(node, obj);
    }

    public static Obj getPointObj(Node node) {
        return pointToObj.get(node);
    }

    public static Set<MethodLevelSite> getMethodNodes(String methodSig) {
        Set<MethodLevelSite> methodLevelSiteSet = methodNodesMap.get(methodSig);
        if (methodLevelSiteSet == null) {
            methodLevelSiteSet = new HashSet<>();
        }
        return methodLevelSiteSet;
    }

    public static List<CallNode> getMethodCallees(String methodSig) {
        return methodCalleeMap.get(methodSig);
    }

    private static boolean ignoredMethodNode(Node node) {
        return node instanceof InstanceField || node instanceof ThisVariable || node instanceof StaticField;
    }
}
