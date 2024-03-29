package org.example.core;

import org.example.core.basic.Global;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.expr.AbstractExprNode;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.MultiExprNode;
import org.example.util.TagUtil;
import soot.*;

import java.util.*;

public class IntraAnalyzedMethod {
    // method base information
    private String name;
    private Body body;
    private String signature;
    private String subSignature;
    private int numberOfParams;
    private List<String> paramTypes = new ArrayList<>();
    private SootMethod methodRef;
    private boolean influenceGlobal;
    private boolean hasReturn;
    private SootClass declaredClassRef;

    // method intra org.example.flow analysis
    private Flow flow;

    public class AnalyzedUnit {
        private Node to;
        private AbstractExprNode from;
        private Unit stmt;

        public AnalyzedUnit(Node to, AbstractExprNode from, Unit stmt) {
            this.to = to;
            this.from = from;
            this.stmt = stmt;
        }


        public Unit getStmt() {
            return stmt;
        }

        public Node getTo() {
            return to;
        }

        public AbstractExprNode getFrom() {
            return from;
        }
    }

    class Flow {
        private Map<Node, AnalyzedUnit> inorderFlowMap = new LinkedHashMap<>();
        private Set<Parameter> paramNodes = new LinkedHashSet<>();

        public void addFlow(Node to, AbstractExprNode from, Unit stmt) {
            if (from == null || from.isEmpty()) {
                return;
            }

            if ((to instanceof Global) && !influenceGlobal) {
                influenceGlobal = true;
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
            addInorderFlowMap(to, from, stmt);
        }

        public void addInorderFlowMap(Node to, AbstractExprNode from, Unit stmt) {
            if (to instanceof UnifyReturn) {
                if (!inorderFlowMap.containsKey(to)) {
                    inorderFlowMap.put(to, new AnalyzedUnit(to, new MultiExprNode(false), stmt));
                }
                inorderFlowMap.get(to).getFrom().addNodes(from.getAllNodes());
            } else {
                inorderFlowMap.put(to, new AnalyzedUnit(to, from, stmt));
            }
        }

        private void addParamNode(Parameter parameter) {
            paramNodes.add(parameter);
        }

        public Set<Parameter> getParamNodes() {
            return paramNodes;
        }
    }

    protected IntraAnalyzedMethod(SootMethod sootMethod) {
        this.name = sootMethod.getName();
        this.signature = sootMethod.getSignature();
        this.subSignature = sootMethod.getSubSignature();
        this.numberOfParams = sootMethod.getParameterCount();
        this.hasReturn = !(sootMethod.getReturnType() instanceof VoidType);
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

        if (sootMethod.isConcrete()) {
            body = sootMethod.retrieveActiveBody();
        }
    }

    public String getBodyStr() {
        if (body == null) {
            return "";
        }
        return body.toString();
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

    public void addFlow(Node to, AbstractExprNode from, Unit stmt) {
        flow.addFlow(to, from, stmt);
    }

    public Map<Node, AnalyzedUnit> getOrderedFlowMap() {
        return flow.inorderFlowMap;
    }

    public List<String> getParamTypes() {
        return paramTypes;
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
