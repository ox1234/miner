package org.example.core;

import org.example.tags.RouteTag;
import soot.SootMethod;

import java.util.HashSet;
import java.util.Set;

public class RouteIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private Set<String> routes;

    protected RouteIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        routes = new HashSet<>();

        RouteTag routeTag = (RouteTag) sootMethod.getTag("routes");
        for (String route : routeTag.getRoutes()) {
            routes.add(String.format("%s:%s", sootMethod.getDeclaringClass(), route));
        }
    }

    public Set<String> getRoutes() {
        return routes;
    }
}
