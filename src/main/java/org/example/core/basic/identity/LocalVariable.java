package org.example.core.basic.identity;

import org.example.core.basic.*;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.LocalAlloc;
import soot.SootMethod;

public class LocalVariable extends MethodLevelSite implements TypeNode, Neo4jNode {
    protected String type;

    protected LocalVariable(String name, SootMethod enclosingMethod, String type) {
        super(name, enclosingMethod.getSignature());
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public AbstractAllocNode convert() {
        return new LocalAlloc(super.getID(), super.getName(), super.getMethodSig());
    }
}
