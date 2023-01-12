package org.example.core.visitor;

import org.example.config.FlowRepository;
import org.example.core.basic.Node;
import org.example.core.basic.identity.RetNodeIdentity;
import org.example.util.NodeUtil;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;

import java.util.List;
import java.util.Set;

public class StmtVisitor extends AbstractStmtSwitch<Void> {
    private static final StmtVisitor instance = new StmtVisitor();
    private static ValueVisitor valueVisitor;

    public SootMethod currentMethod;
    public Unit currentUnit;

    private StmtVisitor() {
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        handleDefinitionStmt(stmt);
    }

    @Override
    public void caseIdentityStmt(IdentityStmt stmt) {
        handleDefinitionStmt(stmt);
    }

    @Override
    public void caseInvokeStmt(InvokeStmt stmt) {
        stmt.getInvokeExpr().apply(valueVisitor);
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        Value op = stmt.getOp();
        op.apply(valueVisitor);

        List<Node> nodeSet = valueVisitor.getResult();
        Node node = new RetNodeIdentity(currentMethod, currentUnit);
        FlowRepository.addTaintFlow(node, nodeSet);
    }

    public void handleDefinitionStmt(DefinitionStmt stmt) {
        Value leftVal = stmt.getLeftOp();
        Value rightVal = stmt.getRightOp();

        leftVal.apply(valueVisitor);
        List<Node> leftNodes = valueVisitor.getResult();
        assert leftNodes.size() == 1;

        rightVal.apply(valueVisitor);
        List<Node> rightNode = valueVisitor.getResult();


        FlowRepository.addTaintFlow(leftNodes.get(0), rightNode);
    }


    public static StmtVisitor getInstance(SootMethod sootMethod, Unit unit) {
        instance.currentMethod = sootMethod;
        instance.currentUnit = unit;
        valueVisitor = ValueVisitor.getInstance(sootMethod, unit);
        return instance;
    }
}
