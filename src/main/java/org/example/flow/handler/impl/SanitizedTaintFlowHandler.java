package org.example.flow.handler.impl;

import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.node.CallNode;
import org.example.core.expr.AbstractExprNode;
import org.example.core.expr.SingleExprNode;
import org.example.flow.Collector;
import org.example.flow.FlowEngine;
import soot.PrimType;
import soot.Type;

import java.util.Set;

public class SanitizedTaintFlowHandler extends TaintFlowHandler {
    private Set<String> sourceSignature;
    private Set<String> sanitizerSignature;

    public SanitizedTaintFlowHandler(FlowEngine flowEngine, Set<String> sourceSignature, Set<String> sanitizerSignature, Collector... collectors) {
        super(flowEngine, collectors);
        this.sourceSignature = sourceSignature;
        this.sanitizerSignature = sanitizerSignature;
    }

    @Override
    public boolean preTransferLeft(Node to, AbstractExprNode from, Boolean result) {
        // if left node is a prime type, will not propagate taint
        if (isPrimType(to)) {
            return false;
        }
        if (from instanceof SingleExprNode && from.getFirstNode() instanceof CallNode) {
            CallNode callNode = (CallNode) from.getFirstNode();
            // if this call is source call, will taint left node
            if (isSourceCall(callNode)) {
                return true;
            }

            // if this call is sanitizer call, will not taint left node
            if (isSanitizerCall(callNode)) {
                return false;
            }
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

    private boolean isSanitizerCall(CallNode callNode) {
        return sanitizerSignature.contains(callNode.getCallee().getSignature());
    }
}
