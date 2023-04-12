package org.example.flow.basic;

import org.example.core.basic.Node;
import org.example.core.basic.field.ArrayLoad;

public class ArrayField {
    private boolean isTaint;
    private ArrayLoad arrayLoad;
    private Node valueNode;

    public ArrayField(ArrayLoad arrayField, Node valueNode) {
        this.arrayLoad = arrayField;
        this.valueNode = valueNode;
    }

    public Node getValueNode() {
        return valueNode;
    }
}
