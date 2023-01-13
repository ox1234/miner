package org.example.core.basic;

import soot.SootMethod;
import soot.Unit;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractNode implements Node {
    protected String id;
    protected String type;
    protected Unit nodeSite;
    protected SootMethod enclosingMethod;
    protected Set<Node> relatedNodes = new LinkedHashSet<>();

    public AbstractNode(String type, SootMethod enclosingMethod, Unit nodeSite) {
        this.type = type;
        this.enclosingMethod = enclosingMethod;
        this.nodeSite = nodeSite;
    }

    @Override
    public String getNodeID() {
        return id;
    }

    @Override
    public SootMethod getEnclosingMethod() {
        return this.enclosingMethod;
    }

    public Unit getNodeSite() {
        return nodeSite;
    }

    @Override
    public void addEdge(Node node) {
        relatedNodes.add(node);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) {
            return false;
        }
        Node node = (Node) obj;
        return node.getNodeID().equals(this.getNodeID());
    }
}
