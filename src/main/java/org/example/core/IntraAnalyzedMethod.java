package org.example.core;

import org.example.config.NodeRepository;
import org.example.config.PointRepository;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.expr.AbstractExprNode;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.Obj;
import org.example.core.expr.MultiExprNode;
import org.example.util.TagUtil;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;

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
        private Map<Node, AbstractExprNode> inorderFlowMap = new LinkedHashMap<>();
        private Set<Parameter> paramNodes = new LinkedHashSet<>();

        public void addFlow(Node to, AbstractExprNode from) {
            if (from == null || from.isEmpty()) {
                return;
            }

            for (Node node : from.getAllNodes()) {
                if (node instanceof Parameter) {
                    addParamNode((Parameter) node);
                }

                if (node instanceof CallNode) {
                    CallNode callNode = (CallNode) node;
                    callNode.setRetVar(to);
                }
            }
            addInorderFlowMap(to, from);
        }

        public void addInorderFlowMap(Node to, AbstractExprNode from) {
            if (to instanceof UnifyReturn) {
                if (!inorderFlowMap.containsKey(to)) {
                    inorderFlowMap.put(to, new MultiExprNode());
                }
                inorderFlowMap.get(to).addNodes(from.getAllNodes());
            } else {
                inorderFlowMap.put(to, from);
            }
        }

        private void addParamNode(Parameter parameter) {
            paramNodes.add(parameter);
        }
    }

    protected IntraAnalyzedMethod(SootMethod sootMethod) {
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

        for (int i = 0; i < sootMethod.getParameterCount(); i++) {
            Node paramNode = Site.getNodeInstance(Parameter.class, i, sootMethod, sootMethod.getParameterType(i));
            assert paramNode != null;
            flow.addParamNode((Parameter) paramNode);
        }
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

    public Map<Node, AbstractExprNode> getOrderedFlowMap() {
        return flow.inorderFlowMap;
    }

    public List<Parameter> getParameterNodes() {
        return new ArrayList<>(flow.paramNodes);
    }

    public static IntraAnalyzedMethod getInstance(SootMethod sootMethod) {
        if (TagUtil.isMyBatisWrapper(sootMethod.getDeclaringClass())) {
            return new MyBatisIntraAnalyzedMethod(sootMethod);
        } else if (TagUtil.isRouteMethod(sootMethod)) {
            return new RouteIntraAnalyzedMethod(sootMethod);
        } else {
            return new IntraAnalyzedMethod(sootMethod);
        }
    }
}
