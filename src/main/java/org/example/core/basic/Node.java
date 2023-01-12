package org.example.core.basic;

public interface Node {
    String getNodeID();

    String getType();

    void addEdge(Node node);
}
