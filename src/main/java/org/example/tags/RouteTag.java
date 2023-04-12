package org.example.tags;

import soot.SootMethod;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.HashSet;
import java.util.Set;

public class RouteTag implements Tag {
    private Set<String> routes;

    public RouteTag(Set<String> routePaths) {
        this.routes = routePaths;
    }

    @Override
    public String getName() {
        return "routes";
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return String.join(",", routes).getBytes();
    }

    public Set<String> getRoutes() {
        return routes;
    }
}
