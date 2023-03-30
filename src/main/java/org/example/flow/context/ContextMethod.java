package org.example.flow.context;

import org.example.config.Global;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;
import org.example.core.basic.field.StaticField;
import org.example.core.basic.identity.UnifyReturn;
import org.example.core.basic.node.CallNode;
import org.example.flow.FlowEngine;
import org.example.flow.PointToContainer;
import org.example.flow.TaintContainer;
import org.example.rule.Sink;
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
    private boolean taintAllParam;
    private TaintContainer taintContainer;
    private PointToContainer pointToContainer;
    private IntraAnalyzedMethod intraAnalyzedMethod;

    public ContextMethod(SootMethod sootMethod, CallNode callNode, Unit callSite) {
        this.sootMethod = sootMethod;
        this.callSite = callSite;
        this.callNode = callNode;
        this.taintNodes = new LinkedHashSet<>();
        this.taintContainer = new TaintContainer();
        this.pointToContainer = new PointToContainer();

        this.intraAnalyzedMethod = FlowEngine.getAnalysedMethod(sootMethod);
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }

    public IntraAnalyzedMethod getIntraAnalyzedMethod() {
        return intraAnalyzedMethod;
    }

    public boolean isTaintAllParam() {
        return taintAllParam;
    }

    public void setTaintAllParam(boolean taintAllParam) {
        this.taintAllParam = taintAllParam;
    }

    public void setIntraAnalyzedMethod(IntraAnalyzedMethod intraAnalyzedMethod) {
        this.intraAnalyzedMethod = intraAnalyzedMethod;
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

    public void setReturnTaint(boolean retTaint) {
        this.retTaint = retTaint;
    }

    public TaintContainer getTaintContainer() {
        return taintContainer;
    }

    public PointToContainer getPointToContainer() {
        return pointToContainer;
    }

    public boolean checkReachSink() {
        Sink sink = Global.sinkMap.get(getSootMethod().getSignature());
        if (sink == null) {
            return false;
        }

        // if base is taint and config defined such sink without no param, will report
        if (getTaintContainer().containsTaint(callNode.getBase()) && sink.index.size() == 0) {
            return true;
        }

        for (int idx : sink.index) {
            if (getTaintContainer().checkIdxParamIsTaint(idx)) {
                return true;
            }
        }
        return false;
    }
}
