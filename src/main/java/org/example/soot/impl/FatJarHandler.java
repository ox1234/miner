package org.example.soot.impl;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FatJarHandler extends AchieveHandler {
    private Path outputPath;

    @Override
    public boolean canHandle(Path path) throws Exception {
        String pathToJar = path.toFile().getAbsolutePath();
        return super.canHandle(path) && (isSpringBootJar(pathToJar) || isTomcatWar(pathToJar));
    }

    private boolean isSpringBootJar(String pathToJar) throws IOException {
        try (ZipFile zipFile = new ZipFile(pathToJar)) {
            ZipEntry entry = zipFile.getEntry("org/springframework/boot/loader/JarLauncher.class");
            return entry != null;
        }
    }

    private boolean isTomcatWar(String pathToWar) throws IOException {
        try (ZipFile zipFile = new ZipFile(pathToWar)) {
            ZipEntry entry = zipFile.getEntry("WEB-INF/web.xml");
            return entry != null;
        }
    }

    @Override
    public List<String> getTargetClassDir(Path path) throws Exception {
        if (outputPath == null) {
            outputPath = extractAchieve(path);
        }

        Path appClassPath = outputPath.resolve("BOOT-INF/classes");
        return Collections.singletonList(appClassPath.toFile().getAbsolutePath());
    }

    @Override
    public List<String> getLibraryClassDir(Path path) throws Exception {
        if (outputPath == null) {
            outputPath = extractAchieve(path);
        }

        Path libClassPath = outputPath.resolve("BOOT-INF/lib");
        Collection<File> libJars = FileUtils.listFiles(libClassPath.toFile(), new String[]{"jar"}, true);
        return getLibClassPathList(libJars);
    }

    private List<String> getLibClassPathList(Collection<File> jars) {
        List<String> files = new ArrayList<>();
        for (File jar : jars) {
            files.add(jar.getAbsolutePath());
        }
        return files;
    }
}
