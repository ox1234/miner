package org.example.core.processor.bytedance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.constant.InvokeType;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayLoad;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.identity.VoidNode;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.MultiExprNode;
import org.example.core.expr.OpExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.core.processor.bytedance.proto.*;

import java.util.*;
import java.util.function.BiConsumer;

public class CodeGraphFlowConsumer implements BiConsumer<Node, IntraAnalyzedMethod.AnalyzedUnit> {
    private final Logger logger = LogManager.getLogger(CodeGraphFlowConsumer.class);
    private final Map<Node, VariableSlot> variables;
    private int cursor;
    private final List<Statement> statementList;
    private IntraAnalyzedMethod analyzedMethod;

    public CodeGraphFlowConsumer(IntraAnalyzedMethod analyzedMethod) {
        this.statementList = new ArrayList<>();
        this.variables = new LinkedHashMap<>();
        this.analyzedMethod = analyzedMethod;
        this.cursor = 0;
    }

    @Override
    public void accept(Node to, IntraAnalyzedMethod.AnalyzedUnit analyzedUnit) {
        if (!(to instanceof ThisVariable)) {
            Statement statement = handleStatement(analyzedUnit);
            if (statement != null) {
                statement.toBuilder().setPos(CodeGraphConverter.convertToPos(analyzedMethod.getMethodRef().getDeclaringClass().getName(), analyzedUnit.getStmt()));
                statementList.add(statement);
            }
        }
    }

    // handle statement
    private Statement handleStatement(IntraAnalyzedMethod.AnalyzedUnit analyzedUnit) {
        List<Node> nodes = new ArrayList<>();
        nodes.add(analyzedUnit.getTo());
        nodes.addAll(analyzedUnit.getFrom().getAllNodes());
        nodes.forEach(node -> {
            if (node instanceof LocalVariable) {
                addNodeID(node);
            } else if (node instanceof InstanceField) {
                addNodeID(((InstanceField) node).getBase());
            } else if (node instanceof ArrayLoad) {
                addNodeID(((ArrayLoad) node).getBaseNode());
            } else if (node instanceof CallNode && ((CallNode) node).getInvokeType() != InvokeType.STATIC_INVOKE) {
                addNodeID(((CallNode) node).getBase());
            }
        });

        Statement statement = null;
        Node to = analyzedUnit.getTo();
        AbstractExprNode exprNode = analyzedUnit.getFrom();
        if (exprNode instanceof SingleExprNode) {
            statement = handleSingleExpr(to, (SingleExprNode) exprNode);
        } else if (exprNode instanceof MultiExprNode) {
            statement = handleMultiExpr(to, (MultiExprNode) exprNode);
        } else if (exprNode instanceof OpExprNode) {
            statement = handleOpExpr(to, (OpExprNode) exprNode);
        }

        if (to instanceof StaticField) {
            statement = Statement.newBuilder().setCommonFlow(handleCommonFlow(to, exprNode)).build();
        } else if (to instanceof InstanceField) {
            statement = Statement.newBuilder().setSetField(handleSetInstanceField((InstanceField) to, exprNode)).build();
        }

        return statement;
    }

    private CommonFlow handleCommonFlow(Node to, AbstractExprNode exprNode) {
        CommonFlow.Builder builder = CommonFlow.newBuilder();
        builder.setTargetId(getNodeID(to));
        exprNode.getAllNodes().forEach(node -> builder.addSourceIds(getNodeID(node)));
        return builder.build();
    }

    private Call handleCall(Node to, CallNode callNode) {
        Call.Builder builder = Call.newBuilder();
        if (callNode.getInvokeType() == InvokeType.INSTANCE_INVOKE || callNode.getInvokeType() == InvokeType.SPECIAL_INVOKE) {
            builder.addArgIds(getNodeID(callNode.getBase()));
            builder.setInvokeMethodName(callNode.getCallee().getName());
        } else {
            builder.setStaticFuncName(callNode.getCallee().getName());
        }
        callNode.getArgs().forEach(node -> builder.addArgIds(getNodeID(node)));
        if (to != null && !(to instanceof VoidNode)) {
            builder.addRetIds(getNodeID(to));
        }
        return builder.build();
    }

    private SetField handleSetInstanceField(InstanceField to, AbstractExprNode exprNode) {
        SetField.Builder builder = SetField.newBuilder();
        builder.setObjectPtrId(getNodeID(to.getBase()));
        builder.setName(to.getName());
        if (exprNode instanceof SingleExprNode) {
            builder.setValueId(getNodeID(exprNode.getFirstNode()));
        }
        return builder.build();
    }

    private GetField handleGetInstanceField(Node to, InstanceField instanceField) {
        GetField.Builder builder = GetField.newBuilder();
        builder.setTargetId(getNodeID(to));
        builder.setObjectPtrId(getNodeID(instanceField.getBase()));
        builder.setName(instanceField.getName());
        return builder.build();
    }

    private Phi handlePhi(Node to, MultiExprNode multiExprNode) {
        Phi.Builder builder = Phi.newBuilder();
        builder.setTargetId(getNodeID(to));
        multiExprNode.getAllNodes().forEach(node -> builder.addSourceIds(getNodeID(node)));
        return builder.build();
    }

    private Operation handleOperation(Node to, OpExprNode opExprNode) {
        Operation.Builder builder = Operation.newBuilder();
        builder.setTargetId(getNodeID(to));
        builder.setOperator(convertToOperator(opExprNode.getOp()));
        return builder.build();
    }

    private Operator convertToOperator(org.example.constant.Operation operation) {
        if (Objects.requireNonNull(operation) == org.example.constant.Operation.NUMBERADD) {
            return Operator.ADD;
        }
        return null;
    }

    // handle expr
    private Statement handleMultiExpr(Node to, MultiExprNode multiExprNode) {
        if (multiExprNode.isPhi()) {
            return Statement.newBuilder().setPhi(handlePhi(to, multiExprNode)).build();
        } else {
            return Statement.newBuilder().setCommonFlow(handleCommonFlow(to, multiExprNode)).build();
        }
    }

    private Statement handleSingleExpr(Node to, SingleExprNode singleExprNode) {
        Node fromNode = singleExprNode.getFirstNode();
        if (fromNode instanceof CallNode) {
            return Statement.newBuilder().setCall(handleCall(to, (CallNode) fromNode)).build();
        } else if (fromNode instanceof InstanceField) {
            return Statement.newBuilder().setGetField(handleGetInstanceField(to, (InstanceField) fromNode)).build();
        } else {
            return Statement.newBuilder().setCommonFlow(handleCommonFlow(to, singleExprNode)).build();
        }
    }

    private Statement handleOpExpr(Node to, OpExprNode opExprNode) {
        return Statement.newBuilder().setOperation(handleOperation(to, opExprNode)).build();
    }

    private void addNodeID(Node node) {
        if (variables.containsKey(node)) {
            return;
        }
        Function.Variable variable = CodeGraphConverter.convertToVariable(node);
        VariableSlot slot = new VariableSlot(variable, cursor);
        variables.put(node, slot);
        cursor++;
    }

    private int getNodeID(Node node) {
        if (variables.containsKey(node)) {
            return variables.get(node).getIdx();
        }
        return -1;
    }

    public List<Statement> getStatementList() {
        return statementList;
    }

    public Map<Node, VariableSlot> getVariables() {
        return variables;
    }


    public static class VariableSlot {
        private final Function.Variable variable;
        private final int idx;

        public VariableSlot(Function.Variable variable, int idx) {
            this.variable = variable;
            this.idx = idx;
        }

        public Function.Variable getVariable() {
            return variable;
        }

        public int getIdx() {
            return idx;
        }
    }

}
