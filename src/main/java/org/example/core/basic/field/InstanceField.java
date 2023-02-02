package org.example.core.basic.field;

import org.example.core.basic.*;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.LocalAlloc;
import soot.SootField;
import soot.SootMethod;

public class InstanceField extends MethodLevelSite implements TypeNode, Neo4jNode {
    protected SootField field;
    protected MethodLevelSite base;

    protected InstanceField(MethodLevelSite baseNode, SootField field, SootMethod enclosingMethod) {
        super(String.format("%s.%s", baseNode.getName(), field.getName()), enclosingMethod.getSignature());
        this.base = baseNode;
        this.field = field;
    }

    @Override
    public String getType() {
        return field.getDeclaringClass().getName();
    }

    @Override
    public AbstractAllocNode convert() {
        AbstractAllocNode baseNode = new LocalAlloc(base.getID(), base.getName(), super.getMethodSig());
        AbstractAllocNode fieldNode = new LocalAlloc(super.getID(), super.getName(), super.getMethodSig());
        fieldNode.addField(baseNode);
        return fieldNode;
    }


    public Node getBase() {
        return base;
    }
}
