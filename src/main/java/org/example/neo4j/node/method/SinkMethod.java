package org.example.neo4j.node.method;

import org.neo4j.ogm.annotation.NodeEntity;
import soot.SootMethod;

public class SinkMethod extends Method {
    boolean isSink = true;
    public SinkMethod(SootMethod sootMethod) {
        super(sootMethod);
    }
}
