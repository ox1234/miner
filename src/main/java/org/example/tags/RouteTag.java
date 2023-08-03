package org.example.tags;

import org.example.config.router.Router;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

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
