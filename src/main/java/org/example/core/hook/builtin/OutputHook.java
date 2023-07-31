package org.example.core.hook.builtin;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Configuration;
import org.example.core.Engine;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.hook.bytedance.EngineHook;
import soot.SootClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;

public class OutputHook implements EngineHook {
    private final Logger logger = LogManager.getLogger(OutputHook.class);

    private List<String> analyzedClasses;
    private List<String> analyzedMethods;
    private Map<String, Map<String, String>> analyzedMethodBody;

    public OutputHook() {
        this.analyzedClasses = new ArrayList<>();
        this.analyzedMethods = new ArrayList<>();
        this.analyzedMethodBody = new HashMap<>();
    }

    @Override
    public boolean enabled() {
        return true;
    }

    @Override
    public void hookClass(SootClass sootClass) {
        analyzedClasses.add(sootClass.getName());
    }

    @Override
    public void hookMethod(IntraAnalyzedMethod analyzedMethod) {
        analyzedMethods.add(analyzedMethod.getSignature());
        String declClassName = analyzedMethod.getMethodRef().getDeclaringClass().getName();
        if (!analyzedMethodBody.containsKey(declClassName)) {
            analyzedMethodBody.put(declClassName, new HashMap<>());
        }
        analyzedMethodBody.get(declClassName).put(analyzedMethod.getSignature(), analyzedMethod.getBodyStr());
    }

    @Override
    public void engineFinish() {
        Path outputPath = Paths.get(Configuration.getOutputPath());
        try {
            Files.write(outputPath.resolve("analyzed_classes.txt"), analyzedClasses);
            Files.write(outputPath.resolve("analyzed_methods.txt"), analyzedMethods);
        } catch (Exception e) {
            logger.error(String.format("run output hook fail: %s", e.getMessage()));
        }

        Path jimpleOutput = outputPath.resolve("jimple_output");
        if (!jimpleOutput.toFile().exists()) {
            jimpleOutput.toFile().mkdirs();
        }
        analyzedMethodBody.forEach((declClass, classMethods) -> {
            StringBuilder sb = new StringBuilder();
            classMethods.forEach((sig, body) -> sb.append(String.format("%s:\n--------------------------------------------------------------------------------\n%s\n--------------------------------------------------------------------------------\n\n", sig, body)));
            Path methodFile = jimpleOutput.resolve(declClass);
            try {
                Files.write(methodFile, sb.toString().getBytes());
            } catch (Exception e) {
                logger.error(String.format("write %s class method jimple body fail: %s", declClass, e.getMessage()));
            }
        });
    }
}
