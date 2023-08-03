package org.example.core;

import org.example.core.hook.bytedance.CodeGraphHook;
import org.example.soot.SootSetup;
import org.junit.jupiter.api.Test;
import soot.Hierarchy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CodeGraphEngineTest {
    private final String testcaseDir = "/Users/bytedance/workplace/java/minertestcase";

    @Test
    public void testcase001() {
        assertDoesNotThrow(() -> runTestCase("testcase001"));
    }


    @Test
    public void unittestsink() {
        assertDoesNotThrow(() -> runTestCase("unitcase_sink"));
    }

    private void runTestCase(String id) throws Exception {
        Path testProjectDir = Paths.get(testcaseDir).resolve(id);
        assertTrue(testProjectDir.toFile().exists());
        clientAnalysis(testProjectDir);
    }

    private void clientAnalysis(Path testProjectDir) throws Exception {
        // setup soot environment
        SootSetup setup = new SootSetup();
        setup.initialize(Collections.singleton(testProjectDir.resolve("target").resolve("classes").toString()));

        // initialize analyze engine and do analysis
        CodeGraphHook codeGraphHook = new CodeGraphHook();

        Hierarchy hierarchy = setup.getHierarchy();
        Engine engine = new Engine(hierarchy);
        engine.addEngineHook(codeGraphHook);

        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();
    }
}
