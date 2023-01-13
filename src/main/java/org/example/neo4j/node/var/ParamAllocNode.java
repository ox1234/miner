package org.example.neo4j.node.var;

import org.example.core.basic.identity.Identity;
import org.example.core.basic.identity.ParameterIdentify;

public class ParamAllocNode extends VarAllocNode {
    public int idx;

    protected ParamAllocNode(ParameterIdentify node) {
        super(node);
        this.idx = node.getIdx();
    }
}
