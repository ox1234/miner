package org.example.soot;

import org.example.config.Global;
import org.example.util.Log;
import org.example.util.MethodUtil;
import org.example.util.TagUtil;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.tagkit.AnnotationTag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SootHelper {
    public static CallGraph buildCallGraph() {
        if (!Global.allReachable) {
            Log.info("all reachable not set, will build call graph from routes");
            Collection<SootClass> sootClasses = Scene.v().getApplicationClasses();
            List<SootMethod> routeMethods = getRouteMethods(sootClasses);
            Log.info("collect %d route methods, will build all route cg", routeMethods.size());
            if (routeMethods.isEmpty()) {
                routeMethods.add(Scene.v().getMainMethod());
            }
            Scene.v().setEntryPoints(routeMethods);
        }
        Log.info("running soot phases......");
        PackManager.v().runPacks();
        Log.info("building method call graph.......");
        return Scene.v().getCallGraph();
    }

    public static Hierarchy buildHierarchy() {
        Log.info("building class hierarchy.......");
        return Scene.v().getActiveHierarchy();
    }

    private static List<SootMethod> getRouteMethods(Collection<SootClass> classes) {
        List<SootMethod> routeMethods = new ArrayList<>();
        for (SootClass sootClass : classes) {
            Log.info("searching %s class route methods with %d classes", sootClass.getName(), classes.size());
            for (AnnotationTag annotationTag : TagUtil.getClassAnnotation(sootClass)) {
                if (TagUtil.isSpringControllerAnnotation(annotationTag)) {
                    for (SootMethod sootMethod : sootClass.getMethods()) {
                        // test single route method
//                        if(!sootMethod.getName().equals("jdbc_sqli_vul")){
//                            continue;
//                        }

                        if (MethodUtil.isRouteMethod(sootMethod)) {
                            Log.info("find %s method is route method", sootMethod.getSignature());
                            routeMethods.add(sootMethod);
                        }
                    }
                }
            }
        }
        Log.info("get %d route methods in project", routeMethods.size());
        return routeMethods;
    }

    public static SootMethod getSootMethod(String signature) {
        return Scene.v().getMethod(signature);
    }
}
