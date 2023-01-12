package org.example.core.basic.identity;

import org.example.core.basic.Node;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

public class FieldIdentity extends Identity {
    public SootField field;
    public Node base;

    public FieldIdentity(String type, SootField field, Node base, SootMethod enclosingMethod, Unit nodeSite) {
        super(String.format("field-%s.%s", base.getNodeID(), field.getName()), type, enclosingMethod, nodeSite);
        this.field = field;
        this.base = base;
    }
}
