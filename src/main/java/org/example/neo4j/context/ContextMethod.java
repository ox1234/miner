package org.example.neo4j.context;

import org.example.config.NodeRepository;
import org.example.core.basic.node.CallNode;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

public abstract class ContextMethod {
    private SootMethod sootMethod;
    private Stmt callSite;
    private CallNode callNode;

    public ContextMethod(SootMethod sootMethod, Stmt callSite) {
        this.sootMethod = sootMethod;
        this.callSite = callSite;
        if (callSite != null) {
            String callNodeID = CallNode.getCallNodeID(callSite);
            this.callNode = (CallNode) NodeRepository.getNode(callNodeID);
        }
    }

    public CallNode getCallNode() {
        return callNode;
    }

    public Stmt getCallSite() {
        return callSite;
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
}
