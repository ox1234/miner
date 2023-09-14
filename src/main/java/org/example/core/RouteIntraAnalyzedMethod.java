package org.example.core;

import org.example.config.entry.RouterEntry;
import org.example.tags.RouteTag;
import soot.SootMethod;

public class RouteIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private RouterEntry routerEntry;

    protected RouteIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        this.routerEntry = ((RouteTag) sootMethod.getTag("router")).getRoute();
    }

    public RouterEntry getRouter() {
        return routerEntry;
    }
}
