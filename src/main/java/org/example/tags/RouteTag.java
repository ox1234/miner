package org.example.tags;

import org.apache.logging.log4j.core.appender.routing.Route;
import org.example.basic.Router;
import soot.SootMethod;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

import java.util.HashSet;
import java.util.Set;

public class RouteTag implements Tag {
    private Router router;

    public RouteTag(Router router) {
        this.router = router;
    }

    @Override
    public String getName() {
        return "router";
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return String.join(",", router.getRoutes()).getBytes();
    }

    public Router getRoute() {
        return router;
    }
}
