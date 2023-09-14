package org.example.config.entry;

import org.example.tags.RouteTag;
import soot.SootClass;
import soot.SootMethod;

import java.util.Set;

public class RouterEntry implements IEntry {
    private SootMethod dispatchMethod;
    private Set<String> routes;

    public RouterEntry(SootMethod dispatchMethod, Set<String> routes) {
        dispatchMethod.addTag(new RouteTag(this));
        this.dispatchMethod = dispatchMethod;
        this.routes = routes;
    }

    public Set<String> getRoutes() {
        return routes;
    }

    @Override
    public SootMethod entryMethod() {
        return dispatchMethod;
    }

    @Override
    public boolean isParamTaint() {
        return true;
    }
}
