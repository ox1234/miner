package org.example.core.basic.field;

import org.example.core.basic.Node;
import org.example.core.basic.SiteLevelSite;

public class ArrayLoad extends SiteLevelSite {
    private Node baseNode;
    private Node idxNode;

    protected ArrayLoad(Node baseNode, Node idx) {
        super(baseNode.getID(), idx.getID());
        this.baseNode = baseNode;
        this.idxNode = idx;
    }

    public Node getBaseNode() {
        return baseNode;
    }

    public Node getIdxNode() {
        return idxNode;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", baseNode.toString(), idxNode.toString());
    }
}
