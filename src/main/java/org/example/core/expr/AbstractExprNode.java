package org.example.core.expr;

import org.example.core.Loc;
import org.example.core.basic.Node;
import org.example.core.basic.obj.Obj;
import soot.jimple.Stmt;

import java.util.List;
import java.util.function.Consumer;

abstract public class AbstractExprNode {
    private List<Node> nodes;

    public AbstractExprNode(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Node getFirstNode() {
        return nodes.get(0);
    }

    public List<Node> getAllNodes() {
        return nodes;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public int size() {
        return nodes.size();
    }

    public boolean isObj() {
        return nodes.size() == 1 && getFirstNode() instanceof Obj;
    }

    public void addNodes(List<Node> addNodes) {
        nodes.addAll(addNodes);
    }
}
