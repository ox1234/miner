package org.example.core;

import org.example.basic.Router;
import org.example.tags.RouteTag;
import soot.SootMethod;

public class RouteIntraAnalyzedMethod extends IntraAnalyzedMethod {
    private Router router;

    protected RouteIntraAnalyzedMethod(SootMethod sootMethod) {
        super(sootMethod);
        this.router = ((RouteTag) sootMethod.getTag("router")).getRoute();
    }

    public Router getRouter() {
        return router;
    }
}
