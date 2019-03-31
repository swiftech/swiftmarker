package com.github.swiftech.swiftmarker;

/**
 * 日志处理，用 enable 来控制是否输出
 * TODO: 日志回调交给调用方去处理日志。
 *
 * @author swiftech 2018-11-29
 **/
public class Logger {

    public static final int LEVEL_DEBUG = 10;
    public static final int LEVEL_INFO = 20;
    public static final int LEVEL_WARN = 30;
    public static final int LEVEL_ERROR = 40;

    private static Logger ins = new Logger();

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

    public void error(String msg) {
        if (level <= LEVEL_ERROR) {
            System.out.printf("[SwiftMarker]   [ERR] %s%n", msg);
        }
    }

    public void warn(String msg) {
        if (level <= LEVEL_WARN) {
            System.out.printf("[SwiftMarker]  [WARN] %s%n", msg);
        }
    }

    public void info(String msg) {
        if (level <= LEVEL_INFO) {
            System.out.printf("[SwiftMarker]  [INFO] %s%n", msg);
        }
    }

    public void debug(String msg) {
        if (level <= LEVEL_DEBUG) {
            System.out.printf("[SwiftMarker] [DEBUG] %s%n", msg);
        }
    }

    public void data(String data) {
        if (level == LEVEL_DEBUG) {
            System.out.println("--------------------------------");
            System.out.println(data);
            System.out.println("--------------------------------");
        }
    }
}
