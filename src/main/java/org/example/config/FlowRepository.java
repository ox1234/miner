package org.example.config;

import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.relation.Call;

import java.util.*;

// FlowRepository 负责所有污点流的传播，以一个Map来表示图结构（类似于指针流）
public class FlowRepository {
    private static Map<Node, Set<Node>> taintFlowMap = new HashMap<>();
    private static Map<Node, Set<CallNode>> callReturnMap = new HashMap<>();
    private static Map<Node, Set<Obj>> pointoMap = new HashMap<>();

    public static void addFlow(Node to, Collection<Node> from) {
        if (from == null || from.isEmpty()) {
            return;
        }

        for (Node node : from) {
            if (node instanceof CallNode) {
                CallNode callNode = (CallNode) node;
                if (to != null) {
                    addCallRetFlow(to, callNode);
                }
                List<Node> args = callNode.getArgs();
                List<Node> params = callNode.getParams();
                assert args.size() == params.size();
                for (int i = 0; i < args.size(); i++) {
                    addTaintFlow(params.get(i), args.get(i));
                }
            } else if (node != null) {
                if (node instanceof Obj) {
                    addPointFlow(to, (Obj) node);
                } else {
                    addTaintFlow(to, node);
                }
            }
        }
    }

    private static void addTaintFlow(Node to, Node from) {
        if (!taintFlowMap.containsKey(to)) {
            taintFlowMap.put(to, new HashSet<>());
        }
        taintFlowMap.get(to).add(from);
    }

    private static void addCallRetFlow(Node to, CallNode from) {
        if (!callReturnMap.containsKey(to)) {
            callReturnMap.put(to, new HashSet<>());
        }
        callReturnMap.get(to).add(from);
    }

    private static void addPointFlow(Node to, Obj obj) {
        if (!pointoMap.containsKey(to)) {
            pointoMap.put(to, new HashSet<>());
        }
        pointoMap.get(to).add(obj);
    }

    public static Map<Node, Set<Node>> getTaintFlowMap() {
        return taintFlowMap;
    }

    public static Map<Node, Set<CallNode>> getCallReturnMap() {
        return callReturnMap;
    }

    public static Map<Node, Set<Obj>> getPointoMap() {
        return pointoMap;
    }
}
