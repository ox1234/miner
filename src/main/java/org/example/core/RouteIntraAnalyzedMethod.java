package org.example.core;

import org.example.tags.RouteTag;
import soot.SootMethod;

import java.util.HashSet;
import java.util.Set;

public class RouteIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private Set<String> routes;

    protected RouteIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        RouteTag routeTag = (RouteTag) sootMethod.getTag("routes");
        routes = routeTag.getRoutes();
    }

    public Set<String> getRoutes() {
        return routes;
    }
}
