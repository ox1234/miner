package org.example.core.visitor;

import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.UnifyReturn;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.*;

import java.util.List;

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
        List<Node> nodeSet = valueVisitor.getResult();
        analyzedMethod.addFlow(null, nodeSet);
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        Value op = stmt.getOp();
        op.apply(valueVisitor);

        List<Node> nodeSet = valueVisitor.getResult();
        if (!(currentMethod.getReturnType() instanceof VoidType)) {
            Node node = Site.getNodeInstance(UnifyReturn.class, currentMethod, currentMethod.getReturnType().toString());
            analyzedMethod.addFlow(node, nodeSet);
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
        stmt.getTarget().apply(this);
    }

    @Override
    public void caseIfStmt(IfStmt stmt) {
        Value op = stmt.getCondition();
        op.apply(valueVisitor);
        stmt.getTarget().apply(this);
    }

    @Override
    public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
        for (IntConstant lookupValue : stmt.getLookupValues()) {
            stmt.getTarget(lookupValue.value).apply(this);
        }
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
        List<Node> leftNodes = valueVisitor.getResult();
        assert leftNodes.size() == 1;

        rightVal.apply(valueVisitor);
        List<Node> rightNode = valueVisitor.getResult();

        analyzedMethod.addFlow(leftNodes.get(0), rightNode);
    }


    public static StmtVisitor getInstance(IntraAnalyzedMethod analyzedMethod, Unit unit) {
        instance.analyzedMethod = analyzedMethod;
        instance.currentMethod = analyzedMethod.getMethodRef();
        instance.currentUnit = unit;
        valueVisitor = ValueVisitor.getInstance(analyzedMethod, unit);
        return instance;
    }
}
