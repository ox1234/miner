package org.example.neo4j.node.var;

public class LocalAlloc extends AbstractAllocNode {
    private String methodSig;

    public LocalAlloc(String nodeID, String name, String methodSig) {
        super(nodeID, name);
        this.methodSig = methodSig;
    }
}
