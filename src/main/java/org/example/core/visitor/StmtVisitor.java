package org.example.core.visitor;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.expr.AbstractExprNode;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.VoidNode;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.*;

public class StmtVisitor extends AbstractStmtSwitch<Void> {
    private static final StmtVisitor instance = new StmtVisitor();
    private static ValueVisitor valueVisitor;

    public IntraAnalyzedMethod analyzedMethod;
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
        AbstractExprNode nodeSet = valueVisitor.getResult();
        Node voidNode = Site.getNodeInstance(VoidNode.class, stmt);
        analyzedMethod.addFlow(voidNode, nodeSet, stmt);
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        Value op = stmt.getOp();
        op.apply(valueVisitor);

        AbstractExprNode nodeSet = valueVisitor.getResult();
        if (!(currentMethod.getReturnType() instanceof VoidType)) {
            Node node = Site.getNodeInstance(UnifyReturn.class, currentMethod, currentMethod.getReturnType());
            assert node != null;
            analyzedMethod.addFlow(node, nodeSet, stmt);
        }
    }

    @Override
    public void caseBreakpointStmt(BreakpointStmt stmt) {
        // do nothing
    }

    @Override
    public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
        stmt.getOp().apply(valueVisitor);
    }

    @Override
    public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
        stmt.getOp().apply(valueVisitor);
    }

    @Override
    public void caseGotoStmt(GotoStmt stmt) {
        // do nothing
    }

    @Override
    public void caseIfStmt(IfStmt stmt) {
        // just visit if condition
        Value op = stmt.getCondition();
        op.apply(valueVisitor);
    }

    @Override
    public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
        // do nothing
    }

    @Override
    public void caseNopStmt(NopStmt stmt) {
        // do nothing
    }

    @Override
    public void caseRetStmt(RetStmt stmt) {
        stmt.getStmtAddress().apply(valueVisitor);
    }

    @Override
    public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
        // do nothing
    }

    @Override
    public void caseTableSwitchStmt(TableSwitchStmt stmt) {
        // do nothing
    }

    @Override
    public void caseThrowStmt(ThrowStmt stmt) {
        Value op = stmt.getOp();
        op.apply(valueVisitor);
    }

    public void handleDefinitionStmt(DefinitionStmt stmt) {
        Value leftVal = stmt.getLeftOp();
        Value rightVal = stmt.getRightOp();

        leftVal.apply(valueVisitor);
        AbstractExprNode leftNodes = valueVisitor.getResult();
        assert leftNodes.size() == 1;

        rightVal.apply(valueVisitor);
        AbstractExprNode rightNode = valueVisitor.getResult();

        Node node = leftNodes.getFirstNode();
        analyzedMethod.addFlow(node, rightNode, stmt);
    }


    public static StmtVisitor getInstance(IntraAnalyzedMethod analyzedMethod, Unit unit) {
        instance.analyzedMethod = analyzedMethod;
        instance.currentMethod = analyzedMethod.getMethodRef();
        instance.currentUnit = unit;
        valueVisitor = ValueVisitor.getInstance(analyzedMethod, unit);
        return instance;
    }
}
