package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

/**
 * Config class
 * @author swiftech 2018-11-26
 **/
public class Config {

    /**
     * true 则输出模板渲染过程中的日志
     */
    private boolean debug = false;

    /**
     * 输入换行符
     */
    private String inputLineBreaker = StringUtils.LF;

    /**
     * 输出换行符
     */
    private String outputLineBreaker = StringUtils.LF;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getInputLineBreaker() {
        return inputLineBreaker;
    }

    public void setInputLineBreaker(String inputLineBreaker) {
        this.inputLineBreaker = inputLineBreaker;
    }

    public String getOutputLineBreaker() {
        return outputLineBreaker;
    }

    public void setOutputLineBreaker(String outputLineBreaker) {
        this.outputLineBreaker = outputLineBreaker;
    }
}
