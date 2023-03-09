package org.example.flow.context;

import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.flow.FlowEngine;
import org.example.flow.TaintContainer;
import org.example.util.Log;
import soot.SootMethod;
import soot.Unit;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ContextMethod {
    private SootMethod sootMethod;
    private Unit callSite;
    private CallNode callNode;
    private Set<Node> taintNodes;
    private boolean retTaint;
    private TaintContainer taintContainer;

    public ContextMethod(SootMethod sootMethod, CallNode callNode, Unit callSite) {
        this.sootMethod = sootMethod;
        this.callSite = callSite;
        this.callNode = callNode;
        this.taintNodes = new LinkedHashSet<>();
        this.taintContainer = new TaintContainer();
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }

    @Override
    public int hashCode() {
        return sootMethod.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ContextMethod) {
            return sootMethod.equals(((ContextMethod) obj).sootMethod);
        }
        return false;
    }

    public boolean returnTaint() {
        return retTaint;
    }

    public TaintContainer getTaintContainer() {
        return taintContainer;
    }
}
