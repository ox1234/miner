package org.example.flow.handler.impl;

import jdk.vm.ci.code.site.Call;
import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.flow.Collector;
import org.example.flow.FlowEngine;
import soot.PrimType;
import soot.Type;

import java.util.HashSet;
import java.util.Set;

public class SanitizedTaintFlowHandler extends TaintFlowHandler {
    private Set<String> sourceSignature = new HashSet<>();

    public SanitizedTaintFlowHandler(FlowEngine flowEngine, Collector... collectors) {
        super(flowEngine, collectors);
    }

    @Override
    public boolean preTransferLeft(Node to, AbstractExprNode from, Boolean result) {
        // if left node is a prime type, will not propagate taint
        if (isPrimType(to)) {
            return false;
        }

        if (from instanceof SingleExprNode && from.getFirstNode() instanceof CallNode) {
            return isSourceCall((CallNode) from.getFirstNode());
        }

        return result;
    }


    private boolean isPrimType(Node to) {
        if (to instanceof TypeNode) {
            Type type = ((TypeNode) to).getType();
            return type instanceof PrimType;
        }
        return false;
    }

    private boolean isSourceCall(CallNode callNode) {
        return sourceSignature.contains(callNode.getCallee().getSignature());
    }
}
