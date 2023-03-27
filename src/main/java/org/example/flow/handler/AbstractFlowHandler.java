package org.example.flow.handler;

import org.example.core.basic.Node;
import org.example.flow.CallStack;
import org.example.flow.FlowEngine;
import org.example.flow.TaintContainer;
import org.example.flow.context.ContextMethod;
import soot.Hierarchy;

abstract public class AbstractFlowHandler implements FlowHandler {
    protected FlowEngine flowEngine;
    protected CallStack callStack;

    protected AbstractFlowHandler(FlowEngine flowEngine) {
        this.flowEngine = flowEngine;
        this.callStack = flowEngine.getCallStack();
    }

    public TaintContainer getTaintContainer() {
        return callStack.peek().getTaintContainer();
    }
}
