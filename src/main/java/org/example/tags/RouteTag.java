package org.example.tags;

import org.example.config.entry.RouterEntry;
import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class RouteTag implements Tag {
    private RouterEntry routerEntry;

    public RouteTag(RouterEntry routerEntry) {
        this.routerEntry = routerEntry;
    }

    @Override
    public String getName() {
        return "router";
    }

    @Override
    public byte[] getValue() throws AttributeValueException {
        return String.join(",", routerEntry.getRoutes()).getBytes();
    }

    public RouterEntry getRoute() {
        return routerEntry;
    }
}
