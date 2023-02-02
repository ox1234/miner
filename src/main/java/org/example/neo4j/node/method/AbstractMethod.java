package org.example.neo4j.node.method;

import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.relation.Call;
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
public abstract class AbstractMethod {
    String name;

    @Id
    String signature;

    String subSig;
    String declaredClass;
    List<String> paramTypes = new ArrayList<>();
    int paramCount;

    @Relationship(type = "Call", direction = Relationship.Direction.OUTGOING)
    Set<Call> callees = new HashSet<>();
    @Relationship(type = "HasVar", direction = Relationship.Direction.OUTGOING)
    Set<AbstractAllocNode> methodVars = new HashSet<>();


    protected AbstractMethod(SootMethod sootMethod) {
        this.name = sootMethod.getName();
        this.signature = sootMethod.getSignature();
        this.subSig = sootMethod.getSubSignature();
        this.declaredClass = sootMethod.getDeclaringClass().getName();
        this.paramCount = sootMethod.getParameterCount();
        for (int i = 0; i < sootMethod.getParameterCount(); i++) {
            paramTypes.add(sootMethod.getParameterType(i).toString());
        }
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public static AbstractMethod getInstance(SootMethod sootMethod) {
        AbstractMethod method = null;
        if (MethodUtil.isRouteMethod(sootMethod)) {
            method = new RouteMethod(sootMethod);
        } else if (MethodUtil.isSinkMethod(sootMethod)) {
            method = new SinkMethod(sootMethod);
        } else if (MethodUtil.isLibraryMethod(sootMethod)) {
            method = new LibraryMethod(sootMethod);
        } else if (MethodUtil.isPhantomMethod(sootMethod)) {
            method = new PhantomMethod(sootMethod);
        } else if (MethodUtil.isAppMethod(sootMethod)) {
            method = new AppMethod(sootMethod);
        }
        return method;
    }
}
