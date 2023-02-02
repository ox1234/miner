package org.example.core.visitor;

import org.example.config.NodeRepository;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.field.ArrayReference;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.*;
import org.example.core.basic.obj.ConstantObj;
import org.example.core.basic.obj.NormalObj;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.shimple.AbstractShimpleValueSwitch;

import java.util.*;

public class ValueVisitor extends AbstractShimpleValueSwitch<List<Node>> {
    private static final ValueVisitor instance = new ValueVisitor();

    public SootMethod currentMethod;
    public Unit currentUnit;

    private ValueVisitor() {
    }

    public static ValueVisitor getInstance(SootMethod sootMethod, Unit unit) {
        instance.currentMethod = sootMethod;
        instance.currentUnit = unit;
        return instance;
    }

    // ----------------------------------------------- value visitor ----------------------------------------------------------------
    @Override
    public void caseLocal(soot.Local v) {
        Node node = NodeRepository.getNode(MethodLevelSite.getLevelSiteID(v.getName(), currentMethod.getSignature()));
        if (node == null) {
            node = Site.getNodeInstance(LocalVariable.class, v.getName(), currentMethod, v.getType().toString());
        }
        this.setNodeResult(node);
    }

    @Override
    public void caseDoubleConstant(DoubleConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType().toString(), currentUnit));
    }

    @Override
    public void caseFloatConstant(FloatConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType().toString(), currentUnit));
    }

    @Override
    public void caseIntConstant(IntConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType().toString(), currentUnit));
    }

    @Override
    public void caseLongConstant(LongConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType().toString(), currentUnit));
    }

    @Override
    public void caseNullConstant(NullConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, "null", v.getType().toString(), currentUnit));
    }

    @Override
    public void caseStringConstant(StringConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType().toString(), currentUnit));
    }

    @Override
    public void caseClassConstant(ClassConstant v) {
        this.setNodeResult(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType().toString(), currentUnit));
    }


    @Override
    public void caseMethodHandle(MethodHandle v) {
        this.setNopResult();
    }

    @Override
    public void caseMethodType(MethodType v) {
        this.setNopResult();
    }

    @Override
    public void caseArrayRef(ArrayRef v) {
        // get base node
        v.getBase().apply(this);
        Node baseNode = this.getResult().get(0);

        v.getIndex().apply(this);
        Node idxNode = this.getResult().get(0);

        this.setNodeResult(Site.getNodeInstance(ArrayReference.class, baseNode, idxNode));
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        this.setNodeResult(Site.getNodeInstance(StaticField.class, v.getField()));
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {
        v.getBase().apply(this);
        Node baseNode = this.getResult().get(0);

        this.setNodeResult(Site.getNodeInstance(InstanceField.class, baseNode, v.getField(), currentMethod));
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
        this.setNodeResult(Site.getNodeInstance(Parameter.class, v.getIndex(), currentMethod, v.getType().toString()));
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        // do nothing
    }

    @Override
    public void caseThisRef(ThisRef v) {
        // TODO handle this
    }

    // --------------------------------------------------------- expr visitor ----------------------------------------------
    @Override
    public void caseNewExpr(NewExpr v) {
        setNodeResult(Site.getNodeInstance(NormalObj.class, v.getBaseType().getSootClass(), currentUnit));
    }

    @Override
    public void caseAddExpr(AddExpr v) {

        Value op1 = v.getOp1();
        Value op2 = v.getOp2();

        op1.apply(this);
        List<Node> nodeList = new ArrayList<>(this.getResult());

        op2.apply(this);
        nodeList.addAll(this.getResult());


        this.setNodeResult(nodeList);
    }

    @Override
    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
        handleInvoke(v);
    }

    @Override
    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
        handleInvoke(v);
    }

    @Override
    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
        handleInvoke(v);
    }

    @Override
    public void caseStaticInvokeExpr(StaticInvokeExpr v) {
        handleInvoke(v);
    }

    @Override
    public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
        List<Node> nodeList = new ArrayList<>();
        if (v.getMethod().getName().equals("makeConcatWithConstants")) {
            for (Value boostrapValue : v.getBootstrapArgs()) {
                boostrapValue.apply(this);
                nodeList.addAll(this.getResult());
            }

            for (Value arg : v.getArgs()) {
                arg.apply(this);
                nodeList.addAll(this.getResult());
            }
            this.setNodeResult(nodeList);
        } else {
            handleInvoke(v);
        }
    }

    public void handleInvoke(InvokeExpr v) {
        SootMethod targetMethod = v.getMethod();
        List<Node> args = new ArrayList<>();
        for (int i = 0; i < v.getArgCount(); i++) {
            Value argValue = v.getArg(i);
            argValue.apply(this);
            List<Node> nodes = getResult();
            assert nodes.size() == 1;
            args.add(nodes.get(0));
        }
        this.setNodeResult(Site.getNodeInstance(CallNode.class, targetMethod, currentMethod, currentUnit, args));
    }

    // TODO: 处理所有expr语句

    @Override
    public void caseAndExpr(AndExpr v) {
        setNopResult();
    }

    @Override
    public void caseCmpExpr(CmpExpr v) {
        setNopResult();
    }

    @Override
    public void caseCmpgExpr(CmpgExpr v) {
        setNopResult();
    }

    @Override
    public void caseCmplExpr(CmplExpr v) {
        setNopResult();
    }

    @Override
    public void caseDivExpr(DivExpr v) {
        setNopResult();
    }

    @Override
    public void caseEqExpr(EqExpr v) {
        setNopResult();
    }

    @Override
    public void caseNeExpr(NeExpr v) {
        setNopResult();
    }

    @Override
    public void caseGeExpr(GeExpr v) {
        setNopResult();
    }

    @Override
    public void caseGtExpr(GtExpr v) {
        setNopResult();
    }

    @Override
    public void caseLeExpr(LeExpr v) {
        setNopResult();
    }

    @Override
    public void caseLtExpr(LtExpr v) {
        setNopResult();
    }

    @Override
    public void caseMulExpr(MulExpr v) {
        setNopResult();
    }

    @Override
    public void caseOrExpr(OrExpr v) {
        setNopResult();
    }

    @Override
    public void caseRemExpr(RemExpr v) {
        setNopResult();
    }

    @Override
    public void caseShlExpr(ShlExpr v) {
        setNopResult();
    }

    @Override
    public void caseShrExpr(ShrExpr v) {
        setNopResult();
    }

    @Override
    public void caseUshrExpr(UshrExpr v) {
        setNopResult();
    }

    @Override
    public void caseSubExpr(SubExpr v) {
        setNopResult();
    }

    @Override
    public void caseXorExpr(XorExpr v) {
        setNopResult();
    }

    @Override
    public void caseCastExpr(CastExpr v) {
        setNopResult();
    }

    @Override
    public void caseInstanceOfExpr(InstanceOfExpr v) {
        setNopResult();
    }

    @Override
    public void caseNewArrayExpr(NewArrayExpr v) {
        setNopResult();
    }

    @Override
    public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
        setNopResult();
    }

    @Override
    public void caseLengthExpr(LengthExpr v) {
        setNopResult();
    }

    @Override
    public void caseNegExpr(NegExpr v) {
        setNopResult();
    }


    // ------------------------------------------------------- result manipulate ------------------------------------------

    public void setNopResult() {
        super.setResult(Collections.emptyList());
    }

    public void setNodeResult(Node... result) {
        List<Node> nodeSet = new ArrayList<>(Arrays.asList(result));
        super.setResult(nodeSet);
    }

    public void setNodeResult(List<Node> results) {
        super.setResult(results);
    }

    public List<Node> getResult() {
        List<Node> result = super.getResult();
        super.setResult(null);
        return result;
    }
}
