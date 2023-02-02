package org.example.core.basic.field;

import org.example.core.basic.Node;
import org.example.core.basic.SiteLevelSite;

public class ArrayReference extends SiteLevelSite {
    protected ArrayReference(Node baseNode, Node idx) {
        super(baseNode.getID(), idx.getID());
    }
}
