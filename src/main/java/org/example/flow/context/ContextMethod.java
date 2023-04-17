package org.example.flow.context;

import org.example.config.Global;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.Loc;
import org.example.core.MyBatisIntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.TypeNode;
import org.example.core.basic.identity.Parameter;
import org.example.core.basic.node.CallNode;
import org.example.core.basic.obj.NormalObj;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;
import org.example.flow.PointToContainer;
import org.example.flow.TaintContainer;
import org.example.rule.Sink;
import org.example.util.MethodUtil;
import soot.*;

import java.util.*;

public abstract class ContextMethod {
    private SootMethod sootMethod;
    private Loc callSite;
    private CallNode callNode;
    private Set<Node> taintNodes;
    private boolean retTaint;
    private boolean baseTaint;
    private boolean paramTaint;
    private TaintContainer taintContainer;
    private PointToContainer pointToContainer;
    private IntraAnalyzedMethod intraAnalyzedMethod;

    public ContextMethod(SootMethod sootMethod, CallNode callNode, Loc callSite) {
        this.sootMethod = sootMethod;
        this.callSite = callSite;
        this.callNode = callNode;
        this.taintNodes = new LinkedHashSet<>();
        this.taintContainer = new TaintContainer();
        this.pointToContainer = new PointToContainer();

        this.intraAnalyzedMethod = FlowEngine.getAnalysedMethod(sootMethod);
    }

    public void setBaseTaint(boolean baseTaint) {
        this.baseTaint = baseTaint;
    }

    public boolean isBaseTaint() {
        return baseTaint;
    }

    public void taintAllParams() {
        paramTaint = true;
    }

    public boolean isAllParamTaint() {
        return paramTaint;
    }

    public SootMethod getSootMethod() {
        return sootMethod;
    }

    public IntraAnalyzedMethod getIntraAnalyzedMethod() {
        return intraAnalyzedMethod;
    }

    public void genFakeParamObj() {
        for (int i = 0; i < intraAnalyzedMethod.getParameterNodes().size(); i++) {
            Parameter parameter = intraAnalyzedMethod.getParameterNodes().get(i);
            Obj refPhantomObj = (Obj) Site.getNodeInstance(PhantomObj.class, parameter.getType(), parameter);
            getPointToContainer().addLocalPointRelation(parameter, Collections.singleton(refPhantomObj));
        }
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

    public CallNode getCallNode() {
        return callNode;
    }
}
