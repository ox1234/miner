package org.example.core.basic.identity;

import org.example.core.basic.MethodLevelSite;
import org.example.core.basic.Neo4jNode;
import org.example.core.basic.TypeNode;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.ParamAlloc;
import soot.SootMethod;

public class Parameter extends MethodLevelSite implements TypeNode, Neo4jNode {
    protected int idx;
    protected String type;

    protected Parameter(int idx, SootMethod enclosingMethod, String type) {
        super(getParameterName(idx), enclosingMethod.getSignature());
        this.idx = idx;
        this.type = type;
    }

    public int getIdx() {
        return idx;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public AbstractAllocNode convert() {
        return new ParamAlloc(super.getID(), super.getName(), super.getMethodSig());
    }

    public static String getParameterName(int idx) {
        return String.format("param-%d", idx);
    }
}
