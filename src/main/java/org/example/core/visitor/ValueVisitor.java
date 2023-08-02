package org.example.core.visitor;

import org.example.constant.InvokeType;
import org.example.constant.Operation;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.Loc;
import org.example.core.basic.node.MapPutCallNode;
import org.example.core.basic.obj.ArrayObj;
import org.example.core.basic.obj.MapCollectionObj;
import org.example.core.expr.*;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.field.ArrayLoad;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.*;
import org.example.core.basic.obj.ConstantObj;
import org.example.core.basic.obj.NormalObj;
import org.example.util.ClassUtil;
import org.example.util.MethodUtil;
import soot.*;
import soot.jimple.*;
import soot.shimple.AbstractShimpleValueSwitch;
import soot.shimple.PhiExpr;

import java.util.*;

public class ValueVisitor extends AbstractShimpleValueSwitch<AbstractExprNode> {
    private static final ValueVisitor instance = new ValueVisitor();

    public SootMethod currentMethod;
    public Loc loc;
    public IntraAnalyzedMethod analyzedMethod;

    private ValueVisitor() {
    }

    public static ValueVisitor getInstance(IntraAnalyzedMethod analyzedMethod, Loc loc) {
        instance.analyzedMethod = analyzedMethod;
        instance.currentMethod = analyzedMethod.getMethodRef();
        instance.loc = loc;
        return instance;
    }

    // ----------------------------------------------- value visitor ----------------------------------------------------------------
    @Override
    public void caseLocal(soot.Local v) {
        Node node;
        if (v.getName().equals("this")) {
            node = Site.getNodeInstance(ThisVariable.class, currentMethod, v.getType());
        } else {
            node = Site.getNodeInstance(LocalVariable.class, v.getName(), currentMethod, v.getType());
        }
        this.setResult(new SingleExprNode(node));
    }

    @Override
    public void caseDoubleConstant(DoubleConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType(), loc)));
    }

    @Override
    public void caseFloatConstant(FloatConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType(), loc)));
    }

    @Override
    public void caseIntConstant(IntConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType(), loc)));
    }

    @Override
    public void caseLongConstant(LongConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType(), loc)));
    }

    @Override
    public void caseNullConstant(NullConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, "null", v.getType(), loc)));
    }

    @Override
    public void caseStringConstant(StringConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType(), loc)));
    }

    @Override
    public void caseClassConstant(ClassConstant v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(ConstantObj.class, String.valueOf(v.value), v.getType(), loc)));
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
        Node baseNode = this.getResult().getFirstNode();

        v.getIndex().apply(this);
        Node idxNode = this.getResult().getFirstNode();

        this.setResult(new SingleExprNode(Site.getNodeInstance(ArrayLoad.class, baseNode, idxNode)));
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(StaticField.class, v.getField())));
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {
        v.getBase().apply(this);
        Node baseNode = this.getResult().getFirstNode();

        this.setResult(new SingleExprNode(Site.getNodeInstance(InstanceField.class, baseNode, v.getField(), currentMethod)));
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
        this.setResult(new SingleExprNode(Site.getNodeInstance(Parameter.class, v.getIndex(), currentMethod, v.getType())));
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        this.setNopResult();
    }

    @Override
    public void caseThisRef(ThisRef v) {
        setResult(new SingleExprNode(Site.getNodeInstance(ClassTypeNode.class, v.getType())));
    }

    // --------------------------------------------------------- expr visitor ----------------------------------------------
    @Override
    public void caseNewExpr(NewExpr v) {
        SootClass baseClass = v.getBaseType().getSootClass();
        if (ClassUtil.isMapClass(baseClass)) {
            setResult(new SingleExprNode(Site.getNodeInstance(MapCollectionObj.class, baseClass, loc)));
        } else {
            setResult(new SingleExprNode(Site.getNodeInstance(NormalObj.class, baseClass, loc)));
        }
    }

    @Override
    public void caseAddExpr(AddExpr v) {

        Value op1 = v.getOp1();
        Value op2 = v.getOp2();

        op1.apply(this);
        List<Node> nodeList = new ArrayList<>(this.getResult().getAllNodes());

        op2.apply(this);
        nodeList.addAll(this.getResult().getAllNodes());

        this.setResult(new OpExprNode(Operation.NUMBERADD, nodeList));
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
                nodeList.addAll(this.getResult().getAllNodes());
            }

            for (Value arg : v.getArgs()) {
                arg.apply(this);
                nodeList.addAll(this.getResult().getAllNodes());
            }
            this.setResult(new OpExprNode(Operation.STRCONCAT, nodeList));
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
            List<Node> nodes = getResult().getAllNodes();
            assert nodes.size() == 1;
            args.add(nodes.get(0));
        }

        Node base = null;
        if (v instanceof InstanceInvokeExpr) {
            ((InstanceInvokeExpr) v).getBase().apply(this);
            List<Node> nodes = getResult().getAllNodes();
            assert nodes.size() == 1;
            base = nodes.get(0);
        } else if (v instanceof StaticInvokeExpr) {
            base = Site.getNodeInstance(ClassTypeNode.class, v.getType());
        }

        if (MethodUtil.isMapPutMethod(targetMethod)) {
            this.setResult(new SingleExprNode(Site.getNodeInstance(MapPutCallNode.class, targetMethod, currentMethod, loc, args, base, InvokeType.getInvokeType(v))));
        } else {
            this.setResult(new SingleExprNode(Site.getNodeInstance(CallNode.class, targetMethod, currentMethod, loc, args, base, InvokeType.getInvokeType(v))));
        }
    }

    @Override
    public void casePhiExpr(PhiExpr e) {
        MultiExprNode multiExprNode = new MultiExprNode(true);
        for (Value value : e.getValues()) {
            value.apply(this);
            multiExprNode.addNodes(getResult().getAllNodes());
        }
        this.setResult(multiExprNode);
    }

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
        v.getOp().apply(this);
    }

    @Override
    public void caseInstanceOfExpr(InstanceOfExpr v) {
        setNopResult();
    }

    @Override
    public void caseNewArrayExpr(NewArrayExpr v) {
        Node node = Site.getNodeInstance(ArrayObj.class, v.getBaseType(), loc);
        setResult(new SingleExprNode(node));
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


    @Override
    public void setResult(AbstractExprNode result) {
        result.setLoc(loc);
        super.setResult(result);
    }

    public void setNopResult() {
        setResult(new EmptyExprNode());
    }

    public AbstractExprNode getResult() {
        AbstractExprNode result = super.getResult();
        super.setResult(null);
        return result;
    }
}
