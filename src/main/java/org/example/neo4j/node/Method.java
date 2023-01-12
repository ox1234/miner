package org.example.neo4j.node;

import org.example.config.Global;
import org.example.util.MethodUtil;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import soot.SootMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NodeEntity
public class Method {
    String name;

    @Id
    String signature;

    String subSig;

    @Relationship(type = "CALL", direction = Relationship.Direction.OUTGOING)
    Set<Method> callees;

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

    public void appendCallee(Method callee) {
        if (this.callees == null) {
            this.callees = new HashSet<>();
        }
        this.callees.add(callee);
    }

    public static Method getInstance(SootMethod sootMethod) {
        Method method = null;
        if (MethodUtil.isRouteMethod(sootMethod)) {
            method = new RouteMethod();
        } else if (MethodUtil.isSinkMethod(sootMethod)) {
            method = new SinkMethod();
        } else if (MethodUtil.isLibraryMethod(sootMethod)) {
            method = new LibraryMethod();
        } else if (MethodUtil.isPhantomMethod(sootMethod)) {
            method = new PhantomMethod();
        } else {
            method = new Method();
        }
        method.callees = new HashSet<>();
        method.signature = sootMethod.getSignature();
        method.subSig = sootMethod.getSubSignature();
        method.name = sootMethod.getName();
        return method;
    }
}
