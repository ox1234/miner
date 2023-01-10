package org.example.neo4j.node;

import org.example.config.Global;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NodeEntity
public class Method {
    String name;

    @Id
    String signature;

    String subSig;
    boolean isSink;

    @Relationship(type = "CALL", direction = Relationship.Direction.OUTGOING)
    List<Method> callees;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSubSig() {
        return subSig;
    }

    public void setSubSig(String subSig) {
        this.subSig = subSig;
    }

    public boolean isSink() {
        return isSink;
    }

    public void setSink(boolean sink) {
        isSink = sink;
    }

    public void appendCallee(Method callee) {
        this.callees.add(callee);
    }

    public static Method getInstance(SootMethod sootMethod) {
        Method method = new Method();
        method.callees = new ArrayList<>();
        method.isSink = Global.sinks.contains(sootMethod.getSignature());
        method.signature = sootMethod.getSignature();
        method.subSig = sootMethod.getSubSignature();
        method.name = sootMethod.getName();
        return method;
    }
}
