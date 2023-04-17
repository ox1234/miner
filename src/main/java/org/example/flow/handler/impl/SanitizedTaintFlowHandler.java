package org.example.flow.handler.impl;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Node;
import org.example.core.basic.TypeNode;
import org.example.core.basic.node.CallNode;
import org.example.flow.Collector;
import org.example.flow.FlowEngine;
import soot.PrimType;
import soot.Type;

public class SanitizedTaintFlowHandler extends TaintFlowHandler {
    public SanitizedTaintFlowHandler(FlowEngine flowEngine, Collector... collectors) {
        super(flowEngine, collectors);
    }

    @Override
    public boolean preTransferLeft(Node to, Boolean from) {
        from = basicSanitizer(to);
        return from;
    }

    // basic sanitizer
    private boolean basicSanitizer(Node to) {
        if (to instanceof TypeNode) {
            Type type = ((TypeNode) to).getType();
            return !(type instanceof PrimType);
        }
        return true;
    }
}
