package org.example.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.basic.obj.Obj;

public class Log {
    private static final Logger logger = LogManager.getRootLogger();

    public static void info(String fmt, Object... args) {
        Log.info(String.format(fmt, args));
    }

    public static void error(String fmt, Object... args) {
        logger.error(String.format(fmt, args));
    }

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warn(String fmt, Object... args) {
        logger.warn(String.format(fmt, args));
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void error(String msg) {
        logger.error(msg);
    }
}
