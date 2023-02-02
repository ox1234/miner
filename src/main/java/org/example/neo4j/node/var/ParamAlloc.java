package org.example.neo4j.node.var;

public class ParamAlloc extends AbstractAllocNode {
    private String methodSig;

    public ParamAlloc(String nodeID, String name, String methodSig) {
        super(nodeID, name);
        this.methodSig = methodSig;
    }
}
