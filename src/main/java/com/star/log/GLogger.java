package com.star.log;

import com.star.config.Config;
import com.star.exception.pojo.ToolException;
import com.star.string.StringUtil;

import java.io.PrintStream;
import java.time.LocalDateTime;

/**
 * @author https://github.com/lazyman/rapid-framework/blob/74970528c487fcebcf50d7add69e55bb16e7da8f/rapid-generator
 *         /rapid-generator/src/main/java/cn/org/rapid_framework/generator/util/GLogger.java
 *         <p>
 *         Created by win7 on 2016/10/7.
 */

public final class GLogger {

    public static final int TRACE = 60;
    public static final int DEBUG = 70;
    public static final int INFO = 80;
    public static final int WARN = 90;
    public static final int ERROR = 100;

    public static int logLevel = INFO;
    public static PrintStream out = System.out;
    public static PrintStream err = System.err;

    private static String logFormat = "[{}] [{}] : {}";

    private GLogger() {
    }

    static {
        try {
            Config config = new Config("config.properties");
            logLevel = toLogLevel(config.getString("log.level", "INFO"));
        } catch (ToolException e) {
            GLogger.warn("no log level Specify,use default level");
        }
    }

    public static void trace(final String str) {

        if (logLevel <= TRACE)
            out.println(StringUtil.format(logFormat, LocalDateTime.now(), "TRACE", str));
    }

    public static void debug(final String str) {
        if (logLevel <= DEBUG)
            out.println(StringUtil.format(logFormat, LocalDateTime.now(), "DEBUG", str));
    }

    public static void info(final String str) {
        if (logLevel <= INFO)
            out.println(StringUtil.format(logFormat, LocalDateTime.now(), "INFO", str));
    }

    public static void warn(final String str) {
        if (logLevel <= WARN)
            err.println(StringUtil.format(logFormat, LocalDateTime.now(), "WARN", str));
    }

    public static void warn(final String str, final Throwable throwable) {
        if (logLevel <= WARN) {
            err.println(StringUtil.format(logFormat, LocalDateTime.now(), "WARN", str));
            throwable.printStackTrace(err);
        }
    }

    public static void error(final String str) {
        if (logLevel <= ERROR)
            err.println(StringUtil.format(logFormat, LocalDateTime.now(), "ERROR", str));
    }

    public static void error(final String str, final Throwable e) {
        if (logLevel <= ERROR) {
            err.println(StringUtil.format(logFormat, LocalDateTime.now(), "ERROR", str));
            e.printStackTrace(err);
        }
    }

    public static void println(final String str) {
        if (logLevel <= INFO) {
            out.println(StringUtil.format(logFormat, LocalDateTime.now(), "INFO", str));
        }
    }

    public static int toLogLevel(final String level) {
        int tmp;
        switch (level) {
            case "TRACE":
                tmp = TRACE;
                break;
            case "DEBUG":
                tmp = DEBUG;
                break;
            case "INFO":
                tmp = INFO;
                break;
            case "WARN":
                tmp = WARN;
                break;
            case "ERROR":
                tmp = ERROR;
                break;
            default:
                tmp = ERROR;
                break;
        }

        return tmp;
    }

    public static void main(String[] args) {
        GLogger.debug("hello world");
    }
}
