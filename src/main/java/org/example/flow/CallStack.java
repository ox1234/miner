package org.example.flow;

import org.example.core.basic.Site;
import org.example.flow.context.ContextMethod;
import org.example.flow.context.InstanceContextMethod;
import org.example.core.basic.Node;
import org.example.core.basic.identity.LocalVariable;
import org.example.core.basic.identity.ThisVariable;
import org.example.core.basic.obj.Obj;
import org.example.core.basic.obj.PhantomObj;
import soot.RefType;
import soot.SootClass;
import soot.Type;

import java.util.*;

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
                LocalVariable localVariable = (LocalVariable) node;
                Type localType = localVariable.getSootType();
                if (localType instanceof RefType) {
                    SootClass refClass = ((RefType) localType).getSootClass();
                    Node refObj = Site.getNodeInstance(PhantomObj.class, refClass, localVariable.getRefStmt());
                    Obj obj1 = (Obj) refObj;
                    obj = Collections.singleton(obj1);
                }
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

    public List<String> toArrString() {
        List<String> cg = new ArrayList<>();
        for (ContextMethod contextMethod : callStack) {
            cg.add(contextMethod.getSootMethod().getSignature());
        }
        return cg;
    }
}
