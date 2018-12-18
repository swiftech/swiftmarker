package com.github.swiftech.swiftmarker;

/**
 * 日志处理，用 enable 来控制是否输出
 * TODO: 日志回调交给调用方去处理日志。
 *
 * @author swiftech 2018-11-29
 **/
public class Logger {

    private static Logger ins = new Logger();

    private boolean isEnabled = false;

    private Logger() {
    }

    public static Logger getInstance() {
        return ins;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void error(String msg) {
        System.out.printf("[SwiftMarker]   [ERR] %s%n", msg);
    }

    public void warn(String msg) {
        System.out.printf("[SwiftMarker]  [WARN] %s%n", msg);
    }

    public void info(String msg) {
        System.out.printf("[SwiftMarker]  [INFO] %s%n", msg);
    }

    public void debug(String msg) {
        if (isEnabled) System.out.printf("[SwiftMarker] [DEBUG] %s%n", msg);
    }

    public void data(String data) {
        if (isEnabled) {
            System.out.println("--------------------------------");
            System.out.println(data);
            System.out.println("--------------------------------");
        }
    }
}
