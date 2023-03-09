package engine;

import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.flow.FlowEngine;
import org.example.soot.SootHelper;
import org.example.soot.SootSetup;
import org.example.soot.impl.SingleClassHandler;
import org.junit.jupiter.api.Test;
import soot.Hierarchy;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class SingleFileEngineTest {

    @Test
    public void analysisSingleFileTest() throws Exception{
        Path cp = Paths.get("/Users/bytedance/workplace/java/sasttestcase/target/classes/Main.class");
        SootSetup sootSetup = new SootSetup(new SingleClassHandler());
        sootSetup.initialize(cp.toFile().getAbsolutePath());

        Hierarchy hierarchy = SootHelper.buildHierarchy();
        CallGraph callGraph = SootHelper.buildCallGraph();
        Engine engine = new Engine(hierarchy, callGraph);
        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();

        FlowEngine flowEngine = new FlowEngine(analyzedMethodSet);
        Scene.v().getEntryPoints().forEach(flowEngine::traverse);
    }
}
