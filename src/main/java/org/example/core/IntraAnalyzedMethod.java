package org.example.core;

import org.example.config.NodeRepository;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Node;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.neo4j.relation.Call;
import soot.SootClass;
import soot.SootMethod;

import java.util.*;

public class IntraAnalyzedMethod {
    // method base information
    private String name;
    private String signature;
    private String subSignature;
    private int numberOfParams;
    private List<String> paramTypes = new ArrayList<>();
    private SootMethod methodRef;
    private SootClass declaredClassRef;

    // method intra flow analysis
    private Flow flow;

    class Flow {
        private Map<Node, Set<Node>> taintFlowMap = new HashMap<>();
        private Map<Node, Set<CallNode>> callReturnMap = new HashMap<>();
        private Map<Node, Obj> pointMap = new HashMap<>();

        public void addFlow(Node to, Collection<Node> from) {
            if (from == null || from.isEmpty()) {
                return;
            }

            for (Node node : from) {
                if (node instanceof CallNode) {
                    CallNode callNode = (CallNode) node;
                    callNode.setRetVar(to);
                } else if (node != null) {
                    if (node instanceof Obj) {
                        addPointFlow(to, (Obj) node);
                    } else {
                        addTaintFlow(to, node);
                    }
                }
            }
        }

        private void addTaintFlow(Node to, Node from) {
            if (!taintFlowMap.containsKey(to)) {
                taintFlowMap.put(to, new HashSet<>());
            }
            taintFlowMap.get(to).add(from);
        }

        private void addCallRetFlow(Node to, CallNode from) {
            if (!callReturnMap.containsKey(to)) {
                callReturnMap.put(to, new HashSet<>());
            }
            callReturnMap.get(to).add(from);
        }

        private void addPointFlow(Node to, Obj obj) {
            pointMap.put(to, obj);
            NodeRepository.addPointTo(to, obj);
        }
    }

    public IntraAnalyzedMethod(SootMethod sootMethod) {
        this.name = sootMethod.getName();
        this.signature = sootMethod.getSignature();
        this.subSignature = sootMethod.getSubSignature();
        this.numberOfParams = sootMethod.getParameterCount();
        for (int i = 0; i < sootMethod.getParameterCount(); i++) {
            this.paramTypes.add(sootMethod.getParameterType(i).toString());
        }
        this.methodRef = sootMethod;
        this.declaredClassRef = sootMethod.getDeclaringClass();
        this.flow = new Flow();
    }

    public String getName() {
        return name;
    }

    public String getSignature() {
        return signature;
    }

    public SootMethod getMethodRef() {
        return methodRef;
    }

    public void addFlow(Node to, Collection<Node> from) {
        flow.addFlow(to, from);
    }

    public Map<Node, Set<Node>> getTaintFlowMap() {
        return flow.taintFlowMap;
    }

    public Map<Node, Set<CallNode>> getCallReturnMap() {
        return flow.callReturnMap;
    }

    public Map<Node, Obj> getPointMap() {
        return flow.pointMap;
    }
}
