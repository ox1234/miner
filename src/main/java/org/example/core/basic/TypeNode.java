package org.example.core.basic;

import soot.Type;

public interface TypeNode {
    Type getType();

    default void resetType(Type type) {
    }
}
