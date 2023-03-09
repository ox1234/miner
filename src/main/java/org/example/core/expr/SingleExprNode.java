package org.example.core.expr;

import org.example.core.basic.Node;

import java.util.Collections;

public class SingleExprNode extends AbstractExprNode {
    public SingleExprNode(Node node) {
        super(Collections.singletonList(node));
    }
}
