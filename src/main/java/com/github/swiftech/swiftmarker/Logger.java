package com.github.swiftech.swiftmarker;

/**
 * 日志处理，用 enable 来控制是否输出
 * 可以设置日志回调交给调用方去处理日志。
 *
 * @author swiftech 2018-11-29
 **/
public class Logger {
    public static final int LEVEL_TRACE = 10;
    public static final int LEVEL_DEBUG = 20;
    public static final int LEVEL_INFO = 30;
    public static final int LEVEL_WARN = 40;
    public static final int LEVEL_ERROR = 50;

    private static final Logger ins = new Logger();

    private LoggerListener loggerListener;

    private int level = LEVEL_DEBUG;

    private Logger() {
    }

    public static Logger getInstance() {
        return ins;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setLoggerListener(LoggerListener loggerListener) {
        this.loggerListener = loggerListener;
    }

    public void error(String msg) {
        if (loggerListener == null) {
            if (level <= LEVEL_ERROR) {
                System.out.printf("[SwiftMarker]   [ERR] %s%n", msg);
            }
        }
        else {

            loggerListener.onError(String.format("[SwiftMarker] %s", msg));
        }
    }

    public void warn(String msg) {
        if (loggerListener == null) {
            if (level <= LEVEL_WARN) {
                System.out.printf("[SwiftMarker]  [WARN] %s%n", msg);
            }
        }
        else {
            loggerListener.onWarn(String.format("[SwiftMarker] %s", msg));
        }
    }

    public void info(String msg) {
        if (loggerListener == null) {
            if (level <= LEVEL_INFO) {
                System.out.printf("[SwiftMarker]  [INFO] %s%n", msg);
            }
        }
        else {
            loggerListener.onInfo(String.format("[SwiftMarker] %s", msg));
        }
    }

    public void debug(String msg) {
        if (loggerListener == null) {
            if (level <= LEVEL_DEBUG) {
                System.out.printf("[SwiftMarker] [DEBUG] %s%n", msg);
            }
        }
        else {
            loggerListener.onDebug(String.format("[SwiftMarker] %s", msg));
        }
    }

    public void trace(String msg) {
        if (loggerListener == null) {
            if (level <= LEVEL_TRACE) {
                System.out.printf("[SwiftMarker] [TRACE] %s%n", msg);
            }
        }
        else {
            loggerListener.onTrace(String.format("[SwiftMarker] %s", msg));
        }
    }

    public boolean isTraceEnabled() {
        return level >= LEVEL_TRACE;
    }

    public boolean isDebugEnabled() {
        return level >= LEVEL_DEBUG;
    }

    public boolean isInfoEnabled() {
        return level >= LEVEL_INFO;
    }

    public boolean isWarnEnabled() {
        return level >= LEVEL_WARN;
    }

    public void data(String data) {
        if (isTraceEnabled()) {
            this.trace("Template: ");
            System.out.println("--------------------------------");
            System.out.println(data);
            System.out.println("--------------------------------");
        }
    }
}
