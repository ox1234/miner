package org.example.core.expr;

import org.example.core.basic.Node;

import java.util.ArrayList;
import java.util.List;

public class MultiExprNode extends AbstractExprNode {
    public MultiExprNode(List<Node> nodes) {
        super(nodes);
    }

    public MultiExprNode() {
        super(new ArrayList<>());
    }
}
