import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.basic.Vulnerability;
import org.example.config.Configuration;
import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.flow.FlowEngine;
import org.example.flow.FlowHandlerEnum;
import org.example.flow.collector.vuln.VulnCollector;
import org.example.flow.handler.FlowHandler;
import org.example.flow.handler.impl.TaintFlowHandler;
import org.example.soot.SootSetup;
import org.junit.jupiter.api.Test;
import soot.Hierarchy;
import soot.Scene;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MinerMainTest {
    private final Logger logger = LogManager.getLogger(VulnCollector.class);
    Path unitRepositoryPath = Paths.get("/Users/bytedance/workplace/java/minertestcase");

    @Test
    public void testCollection() throws Exception {
        testOneUnit(unitRepositoryPath.resolve("unitcase_collection"));
    }

    @Test
    public void testMyBatis() {

    }

    @Test
    public void testSanitizer() {

    }

    @Test
    public void testSpringAutoBinding() {

    }

    private void testOneUnit(Path unitTestPath) throws Exception {
        Path classFilePath = unitTestPath.resolve("target").resolve("classes");

        // do config setup
        Configuration.setOutputPath(unitTestPath.resolve("output").toAbsolutePath().toString());

        // setup soot environment
        SootSetup setup = new SootSetup();
        setup.initialize(Collections.singleton(classFilePath.toAbsolutePath().toString()));

        // extract statement information
        Hierarchy hierarchy = setup.getHierarchy();
        Engine engine = new Engine(hierarchy);
        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();

        // do inter analysis
        setup.getEntries().forEach(iEntry -> {
            FlowEngine flowEngine = new FlowEngine(analyzedMethodSet);
            flowEngine.doAnalysis(iEntry);

            // get scan result
            TaintFlowHandler taintFlowHandler = (TaintFlowHandler) flowEngine.getFlowHandler(FlowHandlerEnum.TAINT_FLOW_HANDLER);
            VulnCollector vulnCollector = taintFlowHandler.getVulnCollector();
            Set<Vulnerability> vulnerabilities = vulnCollector.getVulnerabilities();
            logger.info(String.format("%s entry find %d vulnerability", iEntry.entryMethod().getSignature(), vulnerabilities.size()));
        });
    }
}
