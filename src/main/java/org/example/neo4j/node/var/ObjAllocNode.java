package org.example.neo4j.node.var;

import org.example.core.basic.obj.Obj;

public class ObjAllocNode extends AbstractAllocNode {
    public String type;
    public String name;
    public String allocSite;
    public String enclosingMethod;

    protected ObjAllocNode(Obj node) {
        super(node);
        this.type = node.getType();
        this.allocSite = node.getNodeSite().toString();
        this.name = this.type;
    }
}
