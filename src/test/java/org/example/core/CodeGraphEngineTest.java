package org.example.core;

import org.example.core.processor.bytedance.CodeGraphHook;
import org.example.soot.SootSetup;
import org.example.soot.impl.DirectoryHandler;
import org.example.soot.impl.FatJarHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.function.Executable;
import soot.G;
import soot.Hierarchy;
import soot.Scene;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

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
        SootSetup setup = new SootSetup(new DirectoryHandler());
        setup.initialize(testProjectDir.resolve("target").resolve("classes").toString());

        // initialize analyze engine and do analysis
        CodeGraphHook codeGraphHook = new CodeGraphHook(testProjectDir.toString());

        Hierarchy hierarchy = setup.getHierarchy();
        Engine engine = new Engine(hierarchy);
        engine.addEngineHook(codeGraphHook);

        Map<String, IntraAnalyzedMethod> analyzedMethodSet = engine.extractPointRelation();
    }
}
