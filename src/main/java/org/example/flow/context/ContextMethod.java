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
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.FlowEngine;
import org.example.flow.PointToContainer;
import org.example.flow.TaintContainer;
import org.example.rule.Sink;
import soot.*;

import java.util.*;

public abstract class ContextMethod {
    private SootMethod sootMethod;
    private Loc callSite;
    private CallNode callNode;
    private Set<Node> taintNodes;
    private boolean retTaint;
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

    public boolean checkReachSink() {
        // check mybatis sql injection
        if (intraAnalyzedMethod instanceof MyBatisIntraAnalyzedMethod) {
            MyBatisIntraAnalyzedMethod myBatisIntraAnalyzedMethod = (MyBatisIntraAnalyzedMethod) intraAnalyzedMethod;
            List<String> taintPlaceHolders = new ArrayList<>();
            for (String placeHolder : myBatisIntraAnalyzedMethod.getPlaceHolderList()) {
                if (isTaintMyBatisPlaceHolder(placeHolder, myBatisIntraAnalyzedMethod.getSqlParamMap())) {
                    taintPlaceHolders.add(placeHolder);
                }
            }

            if (!taintPlaceHolders.isEmpty()) {
                return true;
            }
        }

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

    private boolean isTaintMyBatisPlaceHolder(String placeHolder, Map<String, Integer> paramAlias) {
        // check if @Param annotation is exists, if exist will map the parameter
        if (paramAlias.containsKey(placeHolder)) {
            return getTaintContainer().checkIdxParamIsTaint(paramAlias.get(placeHolder));
        }

        // check if is ${_parameter} pattern, will map the first parameter
        if (placeHolder.equals("_parameter")) {
            return getTaintContainer().checkIdxParamIsTaint(0);
        }

        // check if is ${0} pattern, will map the parameter index
        try {
            int idx = Integer.parseInt(placeHolder, 10);
            return getTaintContainer().checkIdxParamIsTaint(idx);
        } catch (NumberFormatException ignored) {
        }

        // single parameter handle
        if (intraAnalyzedMethod.getParameterNodes().size() == 1) {
            Parameter paramNode = intraAnalyzedMethod.getParameterNodes().get(0);
            Type paramType = paramNode.getType();
            // if param is prim type, won't be taint
            if (paramType instanceof PrimType) {
                return false;
            } else if (paramType instanceof RefType) {
                // if param is string type, will check taint container
                if (((RefType) paramType).getSootClass().getName().equals("java.lang.String")) {
                    return getTaintContainer().checkIdxParamIsTaint(0);
                }

                // if param is other type, possibly a POJO, will check field
                Set<Obj> objs = getPointToContainer().getPointRefObj(paramNode);
                for (Obj perObj : objs) {
                    if (perObj.isTaintField(placeHolder)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
