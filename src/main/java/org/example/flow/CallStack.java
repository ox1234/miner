package org.example.flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.basic.Node;
import org.example.core.basic.Site;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import soot.Type;

import java.util.*;

public class CallStack {
    private final Logger logger = LogManager.getLogger(CallStack.class);

    private Stack<ContextMethod> callStack;

    public CallStack() {
        this.callStack = new Stack<>();
    }

    public void push(ContextMethod call) {
        callStack.push(call);
    }

    public ContextMethod pop() {
        return callStack.pop();
    }

    public ContextMethod peek() {
        return callStack.peek();
    }

    public ContextMethod getFirst() {
        return callStack.get(0);
    }

    public Stack<ContextMethod> getCallStack() {
        return callStack;
    }

    public boolean contains(ContextMethod call) {
        return callStack.contains(call);
    }

    public Set<Obj> getRefObjs(Node node) {
        Set<Obj> obj = null;
        // rebase this variable
        if (node instanceof ThisVariable) {
            obj = Collections.singleton(getLastStackObj());
        }

        // get node from point to container
        if (obj == null) {
            PointToContainer pointToContainer = callStack.peek().getPointToContainer();
            obj = pointToContainer.getNodeRefObj(node);
        }


        // can't find an obj, will make a phantom obj
        if (obj.isEmpty()) {
            if (node instanceof LocalVariable) {
                LocalVariable localVariable = (LocalVariable) node;
                Type localType = localVariable.getSootType();
                Node refObj = Site.getNodeInstance(PhantomObj.class, localType, localVariable);
                Obj obj1 = (Obj) refObj;
                obj = Collections.singleton(obj1);
            }
        }
        return obj;
    }

    private Obj getLastStackObj() {
        ContextMethod contextMethod = callStack.peek();
        if (contextMethod instanceof InstanceContextMethod) {
            return ((InstanceContextMethod) contextMethod).getObj();
        }
        return null;
    }
}
