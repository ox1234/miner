package org.example.soot.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;
import org.example.extra.MybatisXMLMapperHandler;
import org.example.soot.TargetHandler;
import org.example.util.JarUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class AchieveHandler extends AbstractTargetHandler {
    private final Logger logger = LogManager.getLogger(AchieveHandler.class);

    public AchieveHandler() {
        registerHandler(new MybatisXMLMapperHandler());
    }

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
            logger.info(String.format("target is a packed file(jar/war), will extract inner class in %s", outputPath));
            if (!outputPath.toFile().exists()) {
                outputPath.toFile().mkdirs();
            } else {
                outputPath.toFile().delete();
                outputPath.toFile().mkdirs();
            }
            extractJar(path, outputPath);
        }

        assert outputPath != null;
        return outputPath;
    }

    private void extractJar(Path jarPath, Path tmpDir) throws IOException {
        JarFile jarFile = new JarFile(jarPath.toFile().getAbsoluteFile());
        Enumeration<JarEntry> entryEnumeration = jarFile.entries();
        while (entryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = entryEnumeration.nextElement();
            Path fullPath = tmpDir.resolve(jarEntry.getName());
            if (!jarEntry.isDirectory()
                    && (jarEntry.getName().endsWith(".class")
                    || jarEntry.getName().endsWith(".jar") || jarEntry.getName().endsWith(".properties") || jarEntry.getName().endsWith(".yml") || jarEntry.getName().endsWith(".xml"))) {
                //对一些特殊配置文件需要额外进行收集
                Path dirName = fullPath.getParent();
                if (dirName == null) {
                    throw new IllegalStateException("Parent of item is outside temp directory.");
                }
                if (!Files.exists(dirName)) {
                    Files.createDirectories(dirName);
                }
                try (OutputStream outputStream = Files.newOutputStream(fullPath)) {
                    copy(jarFile.getInputStream(jarEntry), outputStream);
                    apply(fullPath);
                }
            }
        }
    }

    private void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while ((n = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, n);
        }
    }
}
