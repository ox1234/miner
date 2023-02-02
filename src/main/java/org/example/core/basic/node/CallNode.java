package org.example.core.basic.node;

import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.tags.LocationTag;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CallNode extends UnitLevelSite {
    private SootMethod enclosingMethod;
    private SootMethod targetMethod;
    private String signature;
    private List<Node> args;
    private List<Node> params = new ArrayList<>();
    private Node ret;


    protected CallNode(SootMethod targetMethod, SootMethod enclosingMethod, Unit nodeSite, Collection<Node> args) {
        super(nodeSite.toString(), LocationTag.getLocation(nodeSite));
        this.enclosingMethod = enclosingMethod;
        this.targetMethod = targetMethod;
        this.signature = targetMethod.getSignature();
        for (int i = 0; i < targetMethod.getParameterCount(); i++) {
            params.add(Site.getNodeInstance(Parameter.class, i, targetMethod, targetMethod.getParameterType(i).toString()));
        }
        this.args = new ArrayList<>(args);
        this.ret = Site.getNodeInstance(UnifyReturn.class, targetMethod, targetMethod.getReturnType().toString());
    }

    public Node getRet() {
        return ret;
    }

    public List<Node> getArgs() {
        return args;
    }

    public List<Node> getParams() {
        return params;
    }

    public String getEnclosingMethodSig() {
        return enclosingMethod.getSignature();
    }

}
