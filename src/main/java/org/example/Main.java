package org.example;

import org.apache.commons.io.FileUtils;
import org.example.config.Global;
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
import java.util.function.Consumer;
import java.util.function.Function;

public class Main {
    public static void main(String[] args) throws Exception{
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
        for(SootClass sootClass : Scene.v().getClasses()){
            if(sootClass.getName().startsWith("com.weaver") && !sootClass.isPhantom()){
                sootClass.setApplicationClass();
            }else if(sootClass.isPhantom()){
                sootClass.setPhantomClass();
            }else{
                sootClass.setLibraryClass();
            }
        }

        List<SootMethod> routeMethods = getRouteMethods();
        Scene.v().setEntryPoints(routeMethods);

        PackManager.v().runPacks();

        Graph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        CallGraph callGraph = Scene.v().getCallGraph();
        System.out.printf("adding edges to jgrapht%n");
        callGraph.iterator().forEachRemaining(new Consumer<Edge>() {
            @Override
            public void accept(Edge edge) {
                if(edge.tgt() == null){
                    return;
                }
                if(!edge.src().getDeclaringClass().getName().startsWith("com.weaver")){
                    return;
                }
                g.addVertex(edge.src().getSignature());
                g.addVertex(edge.tgt().getSignature());
                g.addEdge(edge.src().getSignature(), edge.tgt().getSignature());
            }
        });
        Global.cg = g;
        System.out.printf("doing depth first iterator%n");

        GraphMLExporter<String, DefaultEdge> mlExporter = new GraphMLExporter<>(s -> s);
        mlExporter.setVertexAttributeProvider(new Function<String, Map<String, Attribute>>() {
            @Override
            public Map<String, Attribute> apply(String s) {
                Map<String, Attribute> m = new HashMap<>();
                m.put("name", DefaultAttribute.createAttribute(s));
                return m;
            }
        });
        mlExporter.setEdgeIdProvider(new IntegerIdProvider<>(0));
        mlExporter.setEdgeAttributeProvider(e -> {
            Map<String, Attribute> m = new HashMap<>();
            m.put("name", DefaultAttribute.createAttribute("call"));
            return m;
        });
        mlExporter.registerAttribute("name", GraphMLExporter.AttributeCategory.ALL, AttributeType.STRING);

        Writer mlWriter = new FileWriter("/home/flight/.config/Neo4j Desktop/Application/relate-data/dbmss/dbms-e6ce57d4-5e78-4c7b-841a-dacb48958ee6/import/cg.graphml");
        mlExporter.exportGraph(g, mlWriter);


//        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(s -> s);
//        exporter.setVertexAttributeProvider(s -> {
//            Map<String, Attribute> map = new LinkedHashMap<>();
//            map.put("label", DefaultAttribute.createAttribute(s));
//            return map;
//        });
//        Writer writer = new StringWriter();
//        exporter.exportGraph(g, writer);
//        FileOutputStream fileOutputStream = new FileOutputStream("cg.dot");
//        fileOutputStream.write(writer.toString().getBytes());
//        fileOutputStream.close();

//        for(SootMethod entry : routeMethods){
//            System.out.printf("doing depth first vuln find for %s%n", entry.getSignature());
//            try {
//                findInterestingCall(entry);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    public static void findInterestingCall(SootMethod entry) throws Exception{
        Graph<String, DefaultEdge> g = Global.cg;
        DepthFirstIterator<String, DefaultEdge> iterator = new DepthFirstIterator<>(g, entry.getSignature());
        List<String> callStack = new ArrayList<>();
        boolean isInteresting = false;
        while(iterator.hasNext()){
            String call = iterator.next();
            if(Global.sinks.contains(call)){
                for (Object o : iterator.getStack()) {
                    System.out.println(o);
                    callStack.add((String) o);
                }
                isInteresting = true;
            }
        }
        if(isInteresting){
            FileOutputStream fileOutputStream = new FileOutputStream(String.format("vulns/%s.flow", entry.getSignature()));
            fileOutputStream.write(String.join("----------------------------------\n\n", callStack).getBytes());
            fileOutputStream.close();
        }
    }

    public static String getLibClassPathStr(Collection<File> jars){
        List<String> files = new ArrayList<>();
        for(File jar : jars){
            files.add(jar.getAbsolutePath());
        }
        return String.join(":", files);
    }

    public static List<AnnotationTag> getClassAnnotation(SootClass sootClass){
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootClass.getTag("VisibilityAnnotationTag");
        if(annotationTag == null){
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }

    public static List<AnnotationTag> getMethodAnnotation(SootMethod sootMethod){
        VisibilityAnnotationTag annotationTag = (VisibilityAnnotationTag) sootMethod.getTag("VisibilityAnnotationTag");
        if(annotationTag == null){
            return Collections.emptyList();
        }
        return annotationTag.getAnnotations();
    }

    public static List<SootMethod> getRouteMethods(){
        List<SootMethod> routeMethods = new ArrayList<>();
        for(SootClass sootClass : Scene.v().getApplicationClasses()){
//            System.out.printf("%s class has %d method%n", sootClass.getName(), sootClass.getMethods().size());
            for(AnnotationTag annotationTag : getClassAnnotation(sootClass)){
                if(isSpringControllerAnnotation(annotationTag)){
                    for(SootMethod sootMethod : sootClass.getMethods()){
                        for(AnnotationTag methodTag : getMethodAnnotation(sootMethod)){
                            if(isRouteMethodAnnotation(methodTag)){
                                routeMethods.add(sootMethod);
                            }
                        }
                    }
                }
            }
        }
        return routeMethods;
    }

    public static boolean isSpringControllerAnnotation(AnnotationTag tag){
        return Global.rule.filter.controllerClassTags.contains(tag.getType());
    }

    public static boolean isRouteMethodAnnotation(AnnotationTag tag){
        return Global.rule.filter.requestMethodTags.contains(tag.getType());
    }
}