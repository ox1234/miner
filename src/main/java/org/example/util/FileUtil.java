package org.example.util;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.config.Global;

import java.io.File;

public class FileUtil {
    private static final Logger logger = LogManager.getLogger(FileUtils.class);

    public static void deleteDirectory(File directory) {
        try {
            FileUtils.cleanDirectory(directory);
        } catch (Exception e) {
            logger.error(String.format("clean output path fail: %s", e));
        }
    }
}
