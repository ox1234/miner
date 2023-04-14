package org.example.core.basic.node;

import org.example.constant.InvokeType;
import org.example.core.Loc;
import org.example.core.basic.CollectionCall;
import org.example.core.basic.Node;
import soot.SootMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapPutCallNode extends CallNode implements CollectionCall {
    protected MapPutCallNode(SootMethod callee, SootMethod caller, Loc loc, Collection<Node> args, Node base, InvokeType invokeType) {
        super(callee, caller, loc, args, base, invokeType);
    }

    @Override
    public CallNode getCallNode() {
        return this;
    }
}
