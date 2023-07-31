package soot;

import org.example.soot.SootSetup;
import org.example.soot.impl.FatJarHandler;
import org.example.soot.impl.SingleClassHandler;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SootLoaderTest {
    @Test
    public void TestFatJarLoader() throws Exception {
        Path cp = Paths.get("src", "test", "resources", "soot", "fatjar.jar");
        SootSetup sootSetup = new SootSetup(null);
        sootSetup.initialize(cp.toFile().getAbsolutePath());
        assertTrue(Scene.v().getApplicationClasses().size() > 0);
    }

    @Test
    public void TestSingleClassLoader() throws Exception {
        Path cp = Paths.get("src", "test", "resources", "soot", "single", "Main.class");
        SootSetup sootSetup = new SootSetup(null);
        sootSetup.initialize(cp.toFile().getAbsolutePath());
        assertTrue(Scene.v().getApplicationClasses().size() > 0);
    }
}
