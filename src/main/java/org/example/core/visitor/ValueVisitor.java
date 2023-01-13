package org.example.core.visitor;

import org.example.config.FlowRepository;
import org.example.config.NodeRepository;
import org.example.core.basic.CallNode;
import org.example.core.basic.Node;
import org.example.core.basic.identity.*;
import org.example.core.basic.obj.ConstantObj;
import org.example.core.basic.obj.NormalObj;
import org.example.core.basic.obj.Obj;
import org.example.util.MethodUtil;
import org.example.util.UnitUtil;
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
        Identity node = NodeRepository.getIdentity(UnitUtil.getIdentityNodeID(currentMethod, v.getName()));
        if (node == null) {
            node = new NormalIdentity(v.getName(), v.getType().toString(), currentMethod, currentUnit);
        }
        this.setNodeResult(node);
    }

    @Override
    public void caseDoubleConstant(DoubleConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseFloatConstant(FloatConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseIntConstant(IntConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseLongConstant(LongConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseNullConstant(NullConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseStringConstant(StringConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseClassConstant(ClassConstant v) {
        this.setNodeResult(new ConstantObj(v, currentMethod, currentUnit));
    }

    @Override
    public void caseMethodHandle(MethodHandle v) {
        System.err.printf("%s unit is not support", currentUnit);
        this.setNodeResult();
    }

    @Override
    public void caseMethodType(MethodType v) {
        System.err.printf("%s unit is not support", currentUnit);
        this.setNodeResult();
    }

    @Override
    public void caseArrayRef(ArrayRef v) {
        // get base node
        v.getBase().apply(this);
        Node baseNode = this.getResult().get(0);

        v.getIndex().apply(this);
        Node idxNode = this.getResult().get(0);

        Node arrayIdentity = new ArrayIdentity(baseNode.getType(), baseNode, idxNode, currentMethod, currentUnit);
        this.setNodeResult(arrayIdentity);
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        Node sField = new StaticFieldIdentity(v.getField(), currentMethod, currentUnit);
        this.setNodeResult(sField);
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {
        v.getBase().apply(this);
        Node baseNode = this.getResult().get(0);
        Node fieldNode = new FieldIdentity(v.getField().getDeclaringClass().getName(), v.getField(), baseNode, currentMethod, currentUnit);
        this.setNodeResult(fieldNode);
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
        Node paramNode = new ParameterIdentify(v.getType().toString(), v.getIndex(), currentMethod);
        this.setNodeResult(paramNode);
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        super.caseCaughtExceptionRef(v);
    }

    @Override
    public void caseThisRef(ThisRef v) {
        super.caseThisRef(v);
    }

    // --------------------------------------------------------- expr visitor ----------------------------------------------
    @Override
    public void caseNewExpr(NewExpr v) {
        setNodeResult(new NormalObj(v.getBaseType().getSootClass(), currentMethod, currentUnit));
    }

    @Override
    public void caseAddExpr(AddExpr v) {
        List<Node> nodeList = new ArrayList<>();

        Value op1 = v.getOp1();
        Value op2 = v.getOp2();

        op1.apply(this);
        nodeList.addAll(this.getResult());

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
        CallNode callNode = new CallNode(targetMethod, currentMethod, currentUnit);
        for (int i = 0; i < v.getArgCount(); i++) {
            Value argValue = v.getArg(i);
            argValue.apply(this);
            List<Node> nodes = getResult();
            assert nodes.size() == 1;

            FlowRepository.addTaintFlow(UnitUtil.getParameterNodeID(targetMethod, i), Collections.singleton(nodes.get(0).getNodeID()));
            callNode.addArg(nodes.get(0));
        }

        callNode.setRet(new RetNodeIdentity(targetMethod, currentUnit));
        this.setNodeResult(callNode);
    }

    // ------------------------------------------------------- result manipulate ------------------------------------------

    public void setNodeResult(Node... result) {
        List<Node> nodeSet = new ArrayList<>(Arrays.asList(result));
        super.setResult(nodeSet);

        for (Node node : result) {
            addNodeRepository(node);
        }
    }

    private void addNodeRepository(Node node) {
        // check if already has this node, if have just return
        if (NodeRepository.getNode(node.getNodeID()) != null) {
            return;
        }

        if (node instanceof Identity) {
            NodeRepository.addIdentity((Identity) node);
        } else if (node instanceof Obj) {
            NodeRepository.addObj((Obj) node);
        } else if (node instanceof CallNode) {
            NodeRepository.addCallNode((CallNode) node);
        }
        NodeRepository.addNode(node);
    }

    public void setNodeResult(List<Node> results) {
        super.setResult(results);
        for (Node node : results) {
            addNodeRepository(node);
        }
    }

    public void setNodeResult() {
        super.setResult(Collections.emptyList());
    }

    public List<Node> getResult() {
        List<Node> result = super.getResult();
        super.setResult(null);
        return result;
    }
}
