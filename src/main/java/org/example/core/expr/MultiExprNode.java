package org.example.core.expr;

import org.example.core.basic.Node;

import java.util.ArrayList;
import java.util.List;


// MultiExprNode will choose one of the nodes
public class MultiExprNode extends AbstractExprNode {
    private boolean isPhi;

    public MultiExprNode(boolean isPhi) {
        super(new ArrayList<>());
        this.isPhi = isPhi;
    }

    public boolean isPhi() {
        return isPhi;
    }
}
