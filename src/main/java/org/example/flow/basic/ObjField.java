package org.example.flow.basic;

import org.example.core.basic.Node;
import org.example.core.basic.field.InstanceField;

public class ObjField {
    private boolean isTaint;
    private InstanceField instanceField;
    private Node valueNode;

    public ObjField(InstanceField instanceField, Node valueNode) {
        this.instanceField = instanceField;
        this.valueNode = valueNode;
    }

    public ObjField(InstanceField instanceField) {
        this.instanceField = instanceField;
    }

    public Node getValueNode() {
        return valueNode;
    }

    public void setValueNode(Node valueNode) {
        this.valueNode = valueNode;
    }

    public boolean isTaint() {
        return isTaint;
    }

    public void setTaint(boolean taint) {
        isTaint = taint;
    }
}