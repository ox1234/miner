package org.example.core.expr;

import org.example.constant.Operation;
import org.example.core.basic.Node;

import java.util.List;

public class OpExprNode extends AbstractExprNode {
    private Operation op;

    public OpExprNode(Operation op, List<Node> nodes) {
        super(nodes);
        this.op = op;
    }

    public Operation getOp() {
        return op;
    }
}
