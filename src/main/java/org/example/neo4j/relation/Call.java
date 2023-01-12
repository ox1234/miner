package org.example.neo4j.relation;

import org.example.neo4j.node.Method;
import org.neo4j.ogm.annotation.*;

import java.util.List;

@RelationshipEntity(type = "CALL")
public class Call {
    @Id
    @GeneratedValue
    Long id;

    List<String> args;
    List<String> argTypes;

    String callSite;

    @StartNode
    Method src;

    @EndNode
    Method tgt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
