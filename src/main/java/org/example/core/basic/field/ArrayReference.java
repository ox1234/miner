package org.example.core.basic.field;

import org.example.core.basic.Node;
import org.example.core.basic.SiteLevelSite;

public class ArrayReference extends SiteLevelSite {
    private Node baseNode;
    private Node idxNode;

    protected ArrayReference(Node baseNode, Node idx) {
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
        return String.format("%s:%s", baseNode.toString(), idxNode.toString());
    }
}
