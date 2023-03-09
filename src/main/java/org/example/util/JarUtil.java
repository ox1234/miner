package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class JarUtil {
    public static boolean isFatJar(String jarPath) {
        // TODO: check is fat jar
        return false;
    }

    public static void copy(InputStream inputStream, OutputStream outputStream) throws IOException {
        final byte[] buffer = new byte[4096];
        int n;
        while ((n = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, n);
        }
    }

    //遍历解压Jar文件
    public static void extractJar(Path jarPath, Path tmpDir) throws IOException {
        JarFile jarFile = new JarFile(jarPath.toFile().getAbsoluteFile());
        Enumeration<JarEntry> entryEnumeration = jarFile.entries();
        while (entryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = entryEnumeration.nextElement();
            Path fullPath = tmpDir.resolve(jarEntry.getName());
            if (!jarEntry.isDirectory()
                    && (jarEntry.getName().endsWith(".class")
                    || jarEntry.getName().endsWith(".jar") || jarEntry.getName().endsWith(".properties") || jarEntry.getName().endsWith(".yml") || jarEntry.getName().endsWith("pom.xml"))) {
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
                }
            }
        }

    }
}
