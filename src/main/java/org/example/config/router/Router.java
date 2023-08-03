package org.example.config.router;

import org.example.tags.RouteTag;
import soot.SootClass;
import soot.SootMethod;

import java.util.Set;

public class Router {
    private SootClass declaredClass;
    private SootMethod dispatchMethod;
    private Set<String> routes;

    public Router(SootClass declaredClass, SootMethod dispatchMethod, Set<String> routes) {
        dispatchMethod.addTag(new RouteTag(this));
        this.declaredClass = declaredClass;
        this.dispatchMethod = dispatchMethod;
        this.routes = routes;
    }

    public SootClass getDeclaredClass() {
        return declaredClass;
    }

    public SootMethod getDispatchMethod() {
        return dispatchMethod;
    }

    public Set<String> getRoutes() {
        return routes;
    }
}
