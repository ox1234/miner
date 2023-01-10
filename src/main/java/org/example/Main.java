package org.example;

import org.apache.commons.io.FileUtils;
import org.example.config.Global;
import org.example.neo4j.node.Method;
import org.example.neo4j.service.MethodService;
import org.example.rule.Root;
import org.example.rule.RuleUtil;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.IntegerIdProvider;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.graphml.GraphMLExporter;
import org.jgrapht.traverse.DepthFirstIterator;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;

import javax.swing.text.html.HTML;
import javax.swing.text.html.Option;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws Exception {
        generateCG();

    }

    public static void generateCG() throws Exception {
        FileInputStream fileInputStream = new FileInputStream("config.json");
        byte[] buf = new byte[fileInputStream.available()];
        fileInputStream.read(buf);

        Root rule = RuleUtil.getRule(new String(buf));
        Global.rule = rule;
        Global.sinks = Global.getAllSinkSignature();

        Path targetPath = Paths.get("/home/flight/workspace/java/泛微OA_20220228/webapps/ROOT");

        Path appClassPath = targetPath.resolve("WEB-INF/classes");
        Path libClassPath = targetPath.resolve("WEB-INF/lib");

        Collection<File> libJars = FileUtils.listFiles(libClassPath.toFile(), new String[]{"jar"}, true);

        Options.v().set_process_dir(Collections.singletonList(appClassPath.toFile().getAbsolutePath()));
        Options.v().set_soot_classpath(getLibClassPathStr(libJars));
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_allow_phantom_elms(true);
        Options.v().set_whole_program(true);
        Options.v().set_no_bodies_for_excluded(true);

        Scene.v().loadNecessaryClasses();
        System.out.printf("load %d app classes and %d library classes%n", Scene.v().getApplicationClasses().size(), Scene.v().getLibraryClasses().size());
        for (SootClass sootClass : Scene.v().getClasses()) {
            if (sootClass.getName().startsWith("com.weaver") && !sootClass.isPhantom()) {
                sootClass.setApplicationClass();
            } else if (sootClass.isPhantom()) {
                sootClass.setPhantomClass();
            } else {
                sootClass.setLibraryClass();
            }
        }

        List<SootMethod> routeMethods = getRouteMethods();
        Scene.v().setEntryPoints(routeMethods);

        PackManager.v().runPacks();

        CallGraph callGraph = Scene.v().getCallGraph();

        Map<String, Method> neo4jEntities = new HashMap<>();
        callGraph.iterator().forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                if (edge.tgt() == null) {
                    return;
                }
                if (!edge.src().getDeclaringClass().getName().startsWith("com.weaver")) {
                    return;
                }

                String srcSig = edge.src().getSignature();
                if (neo4jEntities.containsKey(srcSig)) {
                    Method srcMethod = neo4jEntities.get(srcSig);
                    srcMethod.appendCallee(Method.getInstance(edge.tgt()));
                } else {
                    neo4jEntities.put(edge.src().getSignature(), Method.getInstance(edge.tgt()));
                }
            }
        });

        MethodService methodService = new MethodService();
        neo4jEntities.forEach((s, method) -> {
            methodService.createMethodNode(method);
        });
    }

    public static String getLibClassPathStr(Collection<File> jars) {
        List<String> files = new ArrayList<>();
        for (File jar : jars) {
            files.add(jar.getAbsolutePath());
        }
        return String.join(":", files);
    }

    public static List<AnnotationTag> getClassAnnotation(SootClass sootClass) {
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootClass.getTag("VisibilityAnnotationTag");
        if (annotationTag == null) {
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }

    public static List<AnnotationTag> getMethodAnnotation(SootMethod sootMethod) {
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootMethod.getTag("VisibilityAnnotationTag");
        if (annotationTag == null) {
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }

    public static List<SootMethod> getRouteMethods() {
        List<SootMethod> routeMethods = new ArrayList<>();
        for (SootClass sootClass : Scene.v().getApplicationClasses()) {
//            System.out.printf("%s class has %d method%n", sootClass.getName(), sootClass.getMethods().size());
            for (AnnotationTag annotationTag : getClassAnnotation(sootClass)) {
                if (isSpringControllerAnnotation(annotationTag)) {
                    for (SootMethod sootMethod : sootClass.getMethods()) {
                        for (AnnotationTag methodTag : getMethodAnnotation(sootMethod)) {
                            if (isRouteMethodAnnotation(methodTag)) {
                                routeMethods.add(sootMethod);
                            }
                        }
                    }
                }
            }
        }
        return routeMethods;
    }

    public static boolean isSpringControllerAnnotation(AnnotationTag tag) {
        return Global.rule.filter.controllerClassTags.contains(tag.getType());
    }

    public static boolean isRouteMethodAnnotation(AnnotationTag tag) {
        return Global.rule.filter.requestMethodTags.contains(tag.getType());
    }
}