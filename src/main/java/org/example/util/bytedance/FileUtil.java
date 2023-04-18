package org.example.util.bytedance;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {
    private static final Logger logger = LogManager.getLogger(FileUtil.class);

    public static void DeCompressTarGzip(Path source, Path target) throws IOException {
        logger.info(String.format("decompress target tar %s to %s path", source, target));

        if (Files.notExists(source)) {
            throw new IOException("您要解压的文件不存在");
        }

        //InputStream输入流，以下四个流将tar.gz读取到内存并操作
        //BufferedInputStream缓冲输入流
        //GzipCompressorInputStream解压输入流
        //TarArchiveInputStream解tar包输入流
        try (InputStream fi = Files.newInputStream(source);
             BufferedInputStream bi = new BufferedInputStream(fi);
             GzipCompressorInputStream gzi = new GzipCompressorInputStream(bi);
             TarArchiveInputStream ti = new TarArchiveInputStream(gzi)) {

            ArchiveEntry entry;
            while ((entry = ti.getNextEntry()) != null) {

                //获取解压文件目录，并判断文件是否损坏
                Path newPath = zipSlipProtect(entry, target);

                if (entry.isDirectory()) {
                    //创建解压文件目录
                    Files.createDirectories(newPath);
                } else {
                    //再次校验解压文件目录是否存在
                    Path parent = newPath.getParent();
                    if (parent != null) {
                        if (Files.notExists(parent)) {
                            Files.createDirectories(parent);
                        }
                    }
                    // 将解压文件输入到TarArchiveInputStream，输出到磁盘newPath目录
                    Files.copy(ti, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    //判断压缩文件是否被损坏，并返回该文件的解压目录
    public static Path zipSlipProtect(ArchiveEntry entry, Path targetDir)
            throws IOException {

        Path targetDirResolved = targetDir.resolve(entry.getName());
        Path normalizePath = targetDirResolved.normalize();

        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("压缩文件已被损坏: " + entry.getName());
        }

        return normalizePath;
    }

    public static Set<String> findLibTargets(Path targetDir, Set<String> relativeJars) throws IOException {
        Set<String> paths = new HashSet<>();
        if (targetDir.toFile().exists()) {
            Files.walkFileTree(targetDir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filepath = file.toAbsolutePath().toString();
                    if (filepath.endsWith(".jar")) {
                        if (relativeJars != null && relativeJars.size() > 0) {
                            for (String jarName : relativeJars) {
                                if (filepath.contains(jarName)) {
                                    paths.add(filepath);
                                }

                            }
                        } else {
                            paths.add(filepath);
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        return paths;
    }
}
