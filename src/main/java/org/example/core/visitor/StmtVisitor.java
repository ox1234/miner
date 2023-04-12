package org.example.core.visitor;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.Loc;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.expr.AbstractExprNode;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.identity.VoidNode;
import soot.SootMethod;
import soot.Value;
import soot.VoidType;
import soot.jimple.*;

public class StmtVisitor extends AbstractStmtSwitch<Void> {
    private static final StmtVisitor instance = new StmtVisitor();
    public IntraAnalyzedMethod analyzedMethod;
    public SootMethod currentMethod;
    public int order;

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
        Value expr = stmt.getInvokeExpr();
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(expr, order));
        expr.apply(valueVisitor);

        AbstractExprNode nodeSet = valueVisitor.getResult();
        Node voidNode = Site.getNodeInstance(VoidNode.class);
        analyzedMethod.addFlow(voidNode, nodeSet);
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        Value op = stmt.getOp();
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(op, order));
        op.apply(valueVisitor);

        AbstractExprNode nodeSet = valueVisitor.getResult();
        if (!(currentMethod.getReturnType() instanceof VoidType)) {
            Node node = Site.getNodeInstance(UnifyReturn.class, currentMethod, currentMethod.getReturnType());
            assert node != null;
            analyzedMethod.addFlow(node, nodeSet);
        }
    }

    @Override
    public void caseBreakpointStmt(BreakpointStmt stmt) {
        // do nothing
    }

    @Override
    public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
        Value op = stmt.getOp();
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(op, order));
        op.apply(valueVisitor);
    }

    @Override
    public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
        Value op = stmt.getOp();
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(op, order));
        op.apply(valueVisitor);
    }

    @Override
    public void caseGotoStmt(GotoStmt stmt) {
        // do nothing
    }

    @Override
    public void caseIfStmt(IfStmt stmt) {
        // just visit if condition
        Value op = stmt.getCondition();
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(op, order));
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
        Value expr = stmt.getStmtAddress();
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(expr, order));
        expr.apply(valueVisitor);
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
        ValueVisitor valueVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(op, order));
        op.apply(valueVisitor);
    }

    public void handleDefinitionStmt(DefinitionStmt stmt) {
        Value leftVal = stmt.getLeftOp();
        Value rightVal = stmt.getRightOp();

        ValueVisitor leftVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(leftVal, order));
        leftVal.apply(leftVisitor);
        AbstractExprNode leftNodes = leftVisitor.getResult();
        assert leftNodes.size() == 1;

        ValueVisitor rightVisitor = ValueVisitor.getInstance(analyzedMethod, new Loc(rightVal, order));
        rightVal.apply(rightVisitor);
        AbstractExprNode rightNode = rightVisitor.getResult();

        Node node = leftNodes.getFirstNode();
        analyzedMethod.addFlow(node, rightNode);
    }


    public static StmtVisitor getInstance(IntraAnalyzedMethod analyzedMethod, int order) {
        instance.analyzedMethod = analyzedMethod;
        instance.order = order;
        instance.currentMethod = analyzedMethod.getMethodRef();
        return instance;
    }
}
