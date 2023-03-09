package org.example.flow;

import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.config.NodeRepository;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import soot.Scene;
import soot.SootClass;

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

    public Obj getBaseRefObj(Node node) {
        Obj obj = null;
        // rebase this variable
        if (node instanceof ThisVariable) {
            obj = getLastStackObj();
        }
        // get point to relation
        if (obj == null) {
            obj = NodeRepository.getPointObj(node);
        }
        // can't find a obj, will make a phantom obj
        if (obj == null) {
            if (node instanceof LocalVariable) {
                SootClass sootClass = Scene.v().getSootClass(((LocalVariable) node).getType());
                obj = new PhantomObj(sootClass);
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
