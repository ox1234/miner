package org.example.config.router;

import soot.SootClass;
import soot.SootMethod;

public interface RouteManager {
    boolean isRouteMethod(SootMethod sootMethod);

    boolean isController(SootClass sootClass);

    Router parseToRouteMethod(SootMethod sootMethod);
}
