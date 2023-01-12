package org.example.core.basic.identity;

import org.example.core.basic.Node;
import soot.SootMethod;
import soot.Unit;

public class ArrayIdentity extends Identity {
    public Node idx;
    public Node base;

    public ArrayIdentity(String type, Node idx, Node base, SootMethod enclosingMethod, Unit nodeSite) {
        super(String.format("arr-%s[%s]", base.getNodeID(), idx.getNodeID()), type, enclosingMethod, nodeSite);
        this.idx = idx;
        this.base = base;
    }
}
