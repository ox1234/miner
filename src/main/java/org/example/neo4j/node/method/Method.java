package org.example.neo4j.node.method;

import org.example.config.Global;
import org.example.config.NodeRepository;
import org.example.core.basic.CallNode;
import org.example.core.basic.Node;
import org.example.neo4j.node.var.AbstractAllocNode;
import org.example.neo4j.node.var.VarAllocNode;
import org.example.neo4j.relation.Call;
import org.example.util.MethodUtil;
import org.example.util.UnitUtil;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.jimple.Stmt;

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
    String declaredClass;
    List<String> paramTypes = new ArrayList<>();
    int paramCount;
    SootMethod sootMethodRef;

    @Relationship(type = "CALL", direction = Relationship.Direction.OUTGOING)
    Set<Call> callees;
    @Relationship(type = "HASVAR", direction = Relationship.Direction.OUTGOING)
    Set<AbstractAllocNode> abstractAllocNodes = new HashSet<>();


    protected Method(SootMethod sootMethod) {
        this.name = sootMethod.getName();
        this.signature = sootMethod.getSignature();
        this.subSig = sootMethod.getSubSignature();
        this.declaredClass = sootMethod.getDeclaringClass().getName();
        this.sootMethodRef = sootMethod;
        this.paramCount = sootMethod.getParameterCount();
        if (!Global.onlyCG) {
            importVarRelation();
        }
    }

    public void importVarRelation() {
        for (Type paramType : this.getSootMethodRef().getParameterTypes()) {
            this.paramTypes.add(paramType.toString());
        }
        for (Node node : NodeRepository.getAllNode()) {
            if (node.getEnclosingMethod().getSignature().equals(signature)) {
                if (node instanceof CallNode) {
                    continue;
                }
                AbstractAllocNode allocNode = AbstractAllocNode.getInstance(node);
                if (allocNode == null) {
                    continue;
                }
                abstractAllocNodes.add(allocNode);
            }
        }
    }


    public SootMethod getSootMethodRef() {
        return sootMethodRef;
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

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSubSig() {
        return subSig;
    }

    public void setSubSig(String subSig) {
        this.subSig = subSig;
    }

    public void appendCallee(Method callee, Stmt callSite) {
        if (this.callees == null) {
            this.callees = new HashSet<>();
        }

        this.callees.add(new Call(this, callee, callSite));
    }

    public List<String> getParamTypes() {
        return paramTypes;
    }

    public static Method getInstance(SootMethod sootMethod) {
        Method method = null;
        if (MethodUtil.isRouteMethod(sootMethod)) {
            method = new RouteMethod(sootMethod);
        } else if (MethodUtil.isSinkMethod(sootMethod)) {
            method = new SinkMethod(sootMethod);
        } else if (MethodUtil.isLibraryMethod(sootMethod)) {
            method = new LibraryMethod(sootMethod);
        } else if (MethodUtil.isPhantomMethod(sootMethod)) {
            method = new PhantomMethod(sootMethod);
        } else {
            method = new Method(sootMethod);
        }
        return method;
    }
}
