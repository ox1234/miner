package org.example.flow;

import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.config.NodeRepository;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import org.example.util.Log;
import soot.Scene;
import soot.SootClass;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

public class CallStack {
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

    public boolean contains(ContextMethod call) {
        return callStack.contains(call);
    }

    public Set<Obj> getBaseRefObj(Node node) {
        Set<Obj> obj = new LinkedHashSet<>();
        // rebase this variable
        if (node instanceof ThisVariable) {
            obj = Collections.singleton(getLastStackObj());
        }
        // get point to relation
        if (obj.isEmpty()) {
            obj = callStack.peek().getPointToContainer().getPointRefObj(node);
        }
        // can't find an obj, will make a phantom obj
        if (obj.isEmpty()) {
            if (node instanceof LocalVariable) {
                obj = Collections.singleton(Obj.getPhantomObj(((LocalVariable) node).getType()));
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
