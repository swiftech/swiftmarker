package com.github.swiftech.swiftmarker;


import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 简易模板引擎
 * 规则：
 * ${xxx} 表示直接用对应的值进行替换
 * 如果用$[xxx]开头的表示用此行作为模板遍历数组
 * 注意：一行只能存在一个数组遍历的情况，如果有多个，只处理第一个。
 *
 * @author swiftech
 */
public class TemplateEngine {

    private Logger log = Logger.getInstance();

    private String template;

    private Config config = new Config();

    public TemplateEngine() {
    }

    public TemplateEngine(String template) {
        this.template = template;
    }

    public TemplateEngine(String template, Config config) {
        this.template = template;
        this.config = config;
    }

    public String process(DataModelHandler dataHandler) {
        if (StringUtils.isBlank(template)) {
            throw new RuntimeException("Template not loaded");
        }

        StringBuilder outputBuf = new StringBuilder();

        log.info("Template: ");
        log.data(template);

        String[] lines;
        if ("\r\n".equals(config.getInputLineBreaker())) {
            lines = StringUtils.splitByWholeSeparatorPreserveAllTokens(template, config.getInputLineBreaker());
        }
        else {
            lines = StringUtils.splitPreserveAllTokens(template, config.getInputLineBreaker());
        }

        //
        if (lines == null || lines.length == 0) {
            return "";
        }

        log.info("Start to process line by line: ");
        StringBuilder stanza = null;
        LoopMatrix linesValues = null;
        boolean inProcessMultiLine = false;
        boolean inProcessLogic = false;
        boolean isLogicTrue = true;
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            log.info(String.format("  Line: '%s'", line));

            // 判断逻辑表达式结尾
            if (inProcessLogic) {
                if (StringUtils.contains(line, Constants.EXP_LOGIC_END)) {
                    inProcessLogic = false; // 逻辑处理结束
                    isLogicTrue = true;// 逻辑标志重置，等待下一次判断
                    line = StringUtils.replace(line, Constants.EXP_LOGIC_END, "");
                }
            }
            if (!isLogicTrue) {
                // 处于逻辑否中，略过这一行
                continue;
            }

            // 先判断是否存在逻辑表达式开头
            String logicKey = StringUtils.substringBetween(line, "?{", "}");
            if (StringUtils.isNotBlank(logicKey)) {
                log.debug("逻辑KEY： " + logicKey);
                inProcessLogic = true;
                if (inProcessMultiLine) {

                    isLogicTrue = dataHandler.isLogicalTrue(linesValues, logicKey);
                }
                else{
                    isLogicTrue = dataHandler.isLogicalTrue(logicKey);
                }
                if (isLogicTrue) {
                    log.debug("逻辑有效");
                    line = StringUtils.replace(line, "?{" + logicKey + "}", "");
                }
                else {
                    log.debug("逻辑无效");
                    continue; // 跳出处理，直到逻辑结束（遇到'?{}'标记）
                }
            }


            // 先判断是否存在数组
            String arrKey = StringUtils.substringBetween(line, "$[", "]");

            // 处理数组-首行
            if (StringUtils.isNotBlank(arrKey)) {
                log.info("    Multi line");
                log.debug("    Array params: " + arrKey);
                stanza = new StringBuilder();
                // 去掉数组标识头部后的行模板
                String subTemp = StringUtils.substringAfter(line, "$[" + arrKey + "]");
                linesValues = dataHandler.onLoop(arrKey);

                // 直接结束了
                if (StringUtils.endsWith(line.trim(), Constants.EXP_LOOP_END)) {
                    log.debug("    end in line");
                    processLineValues(linesValues.getMatrix(), StringUtils.substringBefore(subTemp, "$[]"), outputBuf);
                }
                else {
                    log.debug("    continue lines");
                    inProcessMultiLine = true;
                    if (StringUtils.isBlank(subTemp.trim())) {
                        // 单独的数组头，这一行不需要作为模板处理
                        continue;
                    }
                    else {
                        stanza.append(subTemp).append(StringUtils.LF);
                    }
                }
            }
            else if (inProcessMultiLine) {
                // 处理数组-末行
                if (StringUtils.endsWith(line.trim(), Constants.EXP_LOOP_END)) {
                    log.debug("  >");
                    // 跳出数组段模板循环
                    inProcessMultiLine = false;

                    if (Constants.EXP_LOOP_END.equals(line.trim())) {
                        // 单独的数组尾，这一行不需要作为模板处理
                    }
                    else {
                        log.debug("    the end");
                        stanza.append(StringUtils.substringBefore(line, Constants.EXP_LOOP_END));
                    }
                    processLineValues(linesValues.getMatrix(), stanza.toString(), outputBuf);
                }
                // 处理数组-中间行
                else {
                    log.debug("    ...");
                    stanza.append(line).append(config.getOutputLineBreaker());
                    continue;// 等待下一行一起处理
                }
            }
            // 处理常规参数
            else {
                log.debug("    Single line");
                processLine(line, dataHandler, outputBuf);
            }

            // 特殊处理文末
            if (i == lines.length - 1) {
                log.info("reach to the end");
                int start = outputBuf.length() - config.getOutputLineBreaker().length();
                int end = outputBuf.length();
                outputBuf.delete(start, end);
            }
        }
        return outputBuf.toString();
    }

    /**
     * 处理简单的一行
     *
     * @param line
     * @param lineHandler
     * @param buf
     */
    private void processLine(String line, DataModelHandler lineHandler, StringBuilder buf) {
        if (StringUtils.isBlank(line)) {
            // 处理空行
            buf.append(line).append(config.getOutputLineBreaker());
            return;
        }
        String[] keys = StringUtils.substringsBetween(line, "${", "}");
        // 没有参数，原样不动的返回一行
        if (keys == null || keys.length == 0) {
            log.warn("    No place holders for this line.");
            buf.append(line).append(config.getOutputLineBreaker());
            return;
        }
        log.info("    String params: " + StringUtils.join(keys, ","));
        List<String> values = lineHandler.onKeys(keys);
        String retLine = TextUtils.replaceWith(line, keys, values.toArray(new String[0]));
        log.info("    Render: ");
        log.data(retLine);
        buf.append(retLine).append(config.getOutputLineBreaker());
    }

    /**
     * 处理多行的值
     *
     * @param linesValues    根据数组key获取得到的数据模型数组
     * @param templateStanza 模板片段
     * @param outBuf         输出缓存
     */
    private void processLineValues(List<Map<String, Object>> linesValues,
                                   String templateStanza,
                                   StringBuilder outBuf) {
        if (linesValues == null || linesValues.isEmpty()) {
            // 没有提供参数值，直接输出模板
            outBuf.append(templateStanza).append(config.getOutputLineBreaker());
        }
        else {
            for (Map<String, Object> params : linesValues) {
                String retLine = TextUtils.replaceWith(templateStanza,
                        params.keySet().toArray(new String[0]),
                        params.values().toArray(new String[0]));
                log.info("    Render：");
                log.data(retLine);
                outBuf.append(retLine).append(config.getOutputLineBreaker());
            }
        }
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
