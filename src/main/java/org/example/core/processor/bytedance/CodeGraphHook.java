package org.example.core.processor.bytedance;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.IntraAnalyzedMethod;
import org.example.core.basic.Node;
import org.example.core.processor.bytedance.proto.*;
import org.example.core.processor.bytedance.proto.Package;
import soot.SootClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CodeGraphHook implements EngineHook {
    private final Logger logger = LogManager.getLogger(CodeGraphHook.class);
    private final Map<SootClass, Package.Builder> packageMap;
    private final Set<StructSchema> schemas;
    private final String projectDir;
    private final String repoDataFile;

    public CodeGraphHook(String projectDir) {
        this.packageMap = new HashMap<>();
        this.schemas = new HashSet<>();
        this.projectDir = projectDir;
        this.repoDataFile = "repo.pb";
    }

    @Override
    public void hookClass(SootClass sootClass) {
        if (!packageMap.containsKey(sootClass)) {
            packageMap.put(sootClass, Package.newBuilder().setId(sootClass.getName()));
        }
        schemas.add(CodeGraphConverter.convertToStructSchema(sootClass));
        sootClass.getFields().forEach(sootField -> {
            if (sootField.isStatic()) {
                packageMap.get(sootClass).addGlobals(CodeGraphConverter.convertToGlobalNode(sootField));
            }
        });
    }

    @Override
    public void hookMethod(IntraAnalyzedMethod analyzedMethod) {
        Function function = CodeGraphConverter.convertToFunction(analyzedMethod);
        packageMap.get(analyzedMethod.getMethodRef().getDeclaringClass()).addFunctions(function);
    }

    @Override
    public void engineFinish() {
        Repo.Builder builder = Repo.newBuilder();
        // add struct schema
        schemas.forEach(builder::addStructs);

        // add package
        packageMap.forEach((sootClass, pkgBuilder) -> builder.addPackages(pkgBuilder.build()));

        // add language
        builder.setLanguage(Language.JAVA);

        outputRepoPb(builder.build());
        logger.info("code graph hook finished");
    }

    private void outputRepoPb(Repo repo) {
        Path pbOutput = Paths.get(projectDir).resolve(repoDataFile);
        try {
            Files.write(pbOutput, repo.toByteArray());
        } catch (Exception e) {
            logger.error("output repo protobuf bytes fail");
            e.printStackTrace();
        }
    }
}
