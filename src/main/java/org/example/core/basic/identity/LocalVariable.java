package org.example.core.basic.identity;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Neo4jNode;
import org.example.core.basic.TypeNode;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.LocalAlloc;
import soot.SootMethod;
import soot.Unit;

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
