package org.example.soot.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.config.Global;
import org.example.soot.TargetHandler;
import org.example.util.JarUtil;
import org.example.util.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class AchieveHandler implements TargetHandler {

    @Override
    public boolean canHandle(Path path) throws Exception {
        String targetName = path.toFile().getName();
        return targetName.endsWith(".jar") || targetName.endsWith(".war");
    }

    @Override
    public List<String> getTargetClassDir(Path path) throws Exception {
        return null;
    }

    @Override
    public List<String> getLibraryClassDir(Path path) throws Exception {
        return null;
    }

    protected Path extractAchieve(Path path) throws Exception {
        Path outputPath = null;
        String targetName = path.toFile().getName();
        // check file is jar
        if (targetName.endsWith(".jar") || targetName.endsWith(".war")) {
            outputPath = Paths.get(Global.outputPath).resolve(DigestUtils.sha1Hex(targetName));
            Log.info("target is a packed file(jar/war), will extract inner class in %s", outputPath);
            if (!outputPath.toFile().exists()) {
                outputPath.toFile().mkdirs();
            } else {
                outputPath.toFile().delete();
                outputPath.toFile().mkdirs();
            }
            JarUtil.extractJar(path, outputPath);
        }

        assert outputPath != null;
        return outputPath;
    }
}
