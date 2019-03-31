package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

/**
 * Config class
 *
 * @author swiftech 2018-11-26
 **/
public class Config {

    /**
     * true 则输出模板渲染过程中的日志
     */
    private boolean debug = false;

    /**
     * 日志级别
     */
    private int debugLevel = 0;

    /**
     * 是否在值为 null 或者空的时候渲染表达式（即保留表达式渲染），默认为 true
     */
    private boolean renderExpressionIfValueIsBlank = true;

    /**
     * 输入换行符
     */
    private String inputLineBreaker = StringUtils.LF;

    /**
     * 输出换行符
     */
    private String outputLineBreaker = StringUtils.LF;

    /**
     * @deprecated
     * @return
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @deprecated
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getDebugLevel() {
        return debugLevel;
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
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

    public boolean isRenderExpressionIfValueIsBlank() {
        return renderExpressionIfValueIsBlank;
    }

    public void setRenderExpressionIfValueIsBlank(boolean renderExpressionIfValueIsBlank) {
        this.renderExpressionIfValueIsBlank = renderExpressionIfValueIsBlank;
    }
}
