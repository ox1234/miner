package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.core.basic.identity.ParameterIdentify;
import org.example.util.UnitUtil;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends AbstractNode {
    public String signature;
    public List<Node> args = new ArrayList<>();
    public List<Node> params = new ArrayList<>();
    public Node ret;


    public CallNode(SootMethod targetMethod, SootMethod enclosingMethod, Unit nodeSite) {
        super(targetMethod.getDeclaringClass().getName(), enclosingMethod, nodeSite);
        super.id = UnitUtil.getCallNodeID(enclosingMethod, nodeSite);
        this.signature = targetMethod.getSignature();
        for (int i = 0; i < targetMethod.getParameterCount(); i++) {
            params.add(new ParameterIdentify(targetMethod.getParameterType(0).toString(), i, targetMethod));
        }
    }

    public void setRet(Node ret) {
        this.ret = ret;
    }

    public void addArg(Node arg) {
        this.args.add(arg);
    }
}
