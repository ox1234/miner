package org.example.core;

import org.example.config.NodeRepository;
import org.example.config.PointRepository;
import org.example.core.basic.Node;
import org.example.core.expr.AbstractExprNode;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
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

    // method intra org.example.flow analysis
    private Flow flow;

    class Flow {
        private Map<Node, Set<Node>> taintFlowMap = new LinkedHashMap<>();
        private Map<Node, Set<CallNode>> callNodeMap = new LinkedHashMap<>();
        private Map<Node, AbstractExprNode> inorderFlowMap = new LinkedHashMap<>();
        private Map<Node, Obj> pointMap = new LinkedHashMap<>();
        private List<CallNode> callNodes = new ArrayList<>();

        public void addFlow(Node to, AbstractExprNode from) {
            if (from == null || from.isEmpty()) {
                return;
            }

            for (Node node : from.getAllNodes()) {
                if (node instanceof CallNode) {
                    CallNode callNode = (CallNode) node;
                    callNode.setRetVar(to);
                    addCallNode(callNode);
                } else if (node != null) {
                    Obj refObj = node.getRefObj();
                    if (refObj != null) {
                        to.setRefObj(refObj);
                        addPointFlow(to, refObj);
                    } else {
                        addTaintFlow(to, node);
                    }
                }
            }
            addInorderFlowMap(to, from);
        }

        public void addInorderFlowMap(Node to, AbstractExprNode from) {
            inorderFlowMap.put(to, from);
        }

        private void addCallNode(CallNode callNode) {
            callNodes.add(callNode);
        }

        private void addTaintFlow(Node to, Node from) {
            if (!taintFlowMap.containsKey(to)) {
                taintFlowMap.put(to, new HashSet<>());
            }
            taintFlowMap.get(to).add(from);
        }

        private void addCallRetFlow(Node to, CallNode from) {
            if (!callNodeMap.containsKey(to)) {
                callNodeMap.put(to, new HashSet<>());
            }
            callNodeMap.get(to).add(from);
        }

        private void addPointFlow(Node to, Obj obj) {
            PointRepository.pointoMap.put(to, obj);
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

    public List<CallNode> getCallNodes() {
        return flow.callNodes;
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

    public void addFlow(Node to, AbstractExprNode from) {
        flow.addFlow(to, from);
    }

    public Map<Node, Set<Node>> getTaintFlowMap() {
        return flow.taintFlowMap;
    }

    public Map<Node, Set<CallNode>> getCallReturnMap() {
        return flow.callNodeMap;
    }

    public Map<Node, Obj> getPointMap() {
        return flow.pointMap;
    }

    public Map<Node, AbstractExprNode> getOrderedFlowMap() {
        return flow.inorderFlowMap;
    }
}
