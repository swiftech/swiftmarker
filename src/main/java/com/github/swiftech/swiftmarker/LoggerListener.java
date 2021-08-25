package com.github.swiftech.swiftmarker;

/**
 * @author Allen 2019-08-27
 **/
public interface LoggerListener {

    void onLog(String log);

    void onTrace(String log);

    void onDebug(String log);

    void onInfo(String log);

    void onWarn(String log);

    void onError(String log);
}
