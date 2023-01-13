package org.example.neo4j.relation;

import org.example.config.NodeRepository;
import org.example.core.basic.CallNode;
import org.example.core.basic.Node;
import org.example.neo4j.node.method.Method;
import org.example.util.UnitUtil;
import org.neo4j.ogm.annotation.*;
import soot.Type;
import soot.jimple.Stmt;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RelationshipEntity(type = "CALL")
public class Call {
    @Id
    String id;

    List<String> args = new ArrayList<>();
    List<String> argTypes;

    String callSite;

    @StartNode
    Method src;

    @EndNode
    Method tgt;

    public Call(Method start, Method target, Stmt callSite) {
        this.src = start;
        this.tgt = target;
        this.callSite = callSite.toString();
        this.argTypes = target.getParamTypes();
        this.id = UnitUtil.getCallNodeID(start.getSootMethodRef(), callSite);
        if (callSite.containsInvokeExpr()) {
            String callNodeID = UnitUtil.getCallNodeID(start.getSootMethodRef(), callSite);
            CallNode callNode = NodeRepository.getCallNode(callNodeID);
            if (callNode != null) {
                for (Node node : callNode.args) {
                    this.args.add(node.getNodeID());
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public List<String> getArgTypes() {
        return argTypes;
    }

    public void setArgTypes(List<String> argTypes) {
        this.argTypes = argTypes;
    }

    public String getCallSite() {
        return callSite;
    }

    public void setCallSite(String callSite) {
        this.callSite = callSite;
    }

    public Method getSrc() {
        return src;
    }

    public void setSrc(Method src) {
        this.src = src;
    }

    public Method getTgt() {
        return tgt;
    }

    public void setTgt(Method tgt) {
        this.tgt = tgt;
    }
}
