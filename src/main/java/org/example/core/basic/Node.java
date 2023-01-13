package org.example.core.basic;

import soot.SootMethod;

public interface Node {
    String getNodeID();

    String getType();

    void addEdge(Node node);

    SootMethod getEnclosingMethod();
}
