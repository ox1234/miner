package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;
import org.example.flow.TaintContainer;
import org.example.flow.context.ContextMethod;
import soot.Hierarchy;

abstract public class AbstractFlowHandler implements FlowHandler {
    protected FlowEngine flowEngine;
    protected ContextMethod belongMethod;
    protected TaintContainer taintContainer;

    protected AbstractFlowHandler(FlowEngine flowEngine) {
        this.flowEngine = flowEngine;
    }

    public void injectBelongMethod(ContextMethod belongMethod) {
        this.belongMethod = belongMethod;
        this.taintContainer = belongMethod.getTaintContainer();
    }

    public TaintContainer getTaintContainer() {
        return taintContainer;
    }
}
