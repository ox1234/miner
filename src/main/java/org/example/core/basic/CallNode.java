package org.example.core.basic;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.util.UnitUtil;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;
import java.util.List;

public class CallNode extends AbstractNode {
    public String signature;
    public List<Node> args = new ArrayList<>();
    public Node ret;


    public CallNode(SootMethod targetMethod, SootMethod enclosingMethod, Unit nodeSite) {
        super(targetMethod.getDeclaringClass().getName(), enclosingMethod, nodeSite);
        super.id = String.format("call-%s", UnitUtil.getUniqHash(enclosingMethod, nodeSite));
        this.signature = targetMethod.getSignature();
    }

    public void setRet(Node ret) {
        this.ret = ret;
    }

    public void addArg(Node arg) {
        this.args.add(arg);
    }
}
