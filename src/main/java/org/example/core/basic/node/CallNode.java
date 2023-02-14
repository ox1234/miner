package org.example.core.basic.node;

import org.example.constant.InvokeType;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.UnitLevelSite;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.identity.UnifyReturn;
import org.example.tags.LocationTag;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CallNode extends UnitLevelSite {
    private SootMethod caller;
    private SootMethod callee;
    private String signature;
    private List<Node> args;
    private List<Node> params = new ArrayList<>();
    private Node base;
    private Node unifyRet;
    private Node retVar;
    private InvokeType invokeType;


    protected CallNode(SootMethod callee, SootMethod caller, Unit nodeSite, Collection<Node> args, Node base, InvokeType invokeType) {
        super(nodeSite.toString(), LocationTag.getLocation(nodeSite));
        this.base = base;
        this.caller = caller;
        this.callee = callee;
        this.signature = callee.getSignature();
        for (int i = 0; i < callee.getParameterCount(); i++) {
            params.add(Site.getNodeInstance(Parameter.class, i, callee, callee.getParameterType(i).toString()));
        }
        this.args = new ArrayList<>(args);
        if (!(callee.getReturnType() instanceof VoidType)) {
            this.unifyRet = Site.getNodeInstance(UnifyReturn.class, callee, callee.getReturnType().toString());
        }
        this.invokeType = invokeType;
    }

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public Node getRetVar() {
        return retVar;
    }

    public void setRetVar(Node retVar) {
        this.retVar = retVar;
    }

    public Node getUnifyRet() {
        return unifyRet;
    }

    public List<Node> getArgs() {
        return args;
    }

    public List<Node> getParams() {
        return params;
    }

    public String getEnclosingMethodSig() {
        return caller.getSignature();
    }

    public static String getCallNodeID(Unit nodeSite) {
        return getLevelSiteID(nodeSite.toString(), LocationTag.getLocation(nodeSite));
    }

    public Node getBase() {
        return base;
    }

    public SootMethod getCaller() {
        return caller;
    }

    public SootMethod getCallee() {
        return callee;
    }
}
