package org.example.neo4j.node.method;

import soot.SootMethod;

public class RouteMethod extends Method {
    boolean isRoute = true;
    public RouteMethod(SootMethod sootMethod) {
        super(sootMethod);
    }
}
