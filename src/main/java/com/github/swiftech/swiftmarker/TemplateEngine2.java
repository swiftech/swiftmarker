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
 * 1. 循环可以嵌套循环
 * 2. 逻辑判断可以嵌套逻辑判断
 * 3. 循环和逻辑判断可以互相嵌套（如果逻辑判断在循环中取值的话，则取子数据集中的数据）
 *
 * @author swiftech
 */
public class TemplateEngine2 {

    private Logger log = Logger.getInstance();

    private String template;

    private Config config = new Config();

    public TemplateEngine2() {
    }

    public TemplateEngine2(String template) {
        this.template = template;
    }

    public TemplateEngine2(String template, Config config) {
        this.template = template;
        this.config = config;
    }

    public String process(DataModelHandler dataHandler) {
        if (StringUtils.isBlank(template)) {
            throw new RuntimeException("Template not loaded");
        }

        log.info("Config: ");

        log.info("  Input line breaker:" + (StringUtils.LF.equals(config.getInputLineBreaker()) ? "LF" : "CRLF"));
        log.info("  Output line breaker:" + (StringUtils.LF.equals(config.getOutputLineBreaker()) ? "LF" : "CRLF"));

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
        RenderContext ctx = new RenderContext();
        ctx.init();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            log.info(String.format("  Line: '%s'", line));
            ctx.setLine(line);

            // 先判断逻辑退出，因为需要实现逻辑表达式内嵌套其他表达式
            if (ctx.isOutLogic()) {
                log.info("    quitting logic");
                if (ctx.isInLoop()) {
                    ctx.appendToBuffer(line).append(config.getInputLineBreaker());
                }
                else {
                    ctx.popLogicState();
                }
            }
            else {
                if (ctx.isLogicFalse()) {
                    continue;
                }
                // 处理循环表达式开始
                if (ctx.isStartLoop()) {
                    log.info("    entering loop...");
                    String loopKey = ctx.getLoopKey();
                    log.debug("    loop key: " + loopKey);
                    // 去掉数组标识头部后的行模板
                    String subTemp = StringUtils.substringAfter(line, "$[" + loopKey + "]");
                    LoopMatrix loopMatrix = dataHandler.onLoop(loopKey);

                    dataHandler.pushDataModel(loopMatrix);
                    ctx.createBuffer();
                    ctx.appendToBuffer(subTemp);

                    // 直接结束了
                    if (ctx.isOutLoop()) {
                        log.debug("    end in line");
                        StringBuilder outputBuf = ctx.popBuffer();
                        String rendered = processLoop(StringUtils.substringBefore(subTemp, "$[]"), loopMatrix);
                        ctx.appendToBuffer(rendered);
                        ctx.popLoopState();
                        dataHandler.popDataModel();
                    }
                    else {
                        log.debug("    continue lines");
                        if (StringUtils.isBlank(subTemp.trim())) {
                            // 单独的数组头，这一行不需要作为模板处理
                            continue;
                        }
                        else {
                            ctx.appendToBuffer(subTemp).append(config.getOutputLineBreaker());
                        }
                    }
                }
                // 处理循环表达式结尾
                else if (ctx.isOutLoop()) {
                    log.debug("    end loop");
                    StringBuilder stanzaBuf = ctx.popBuffer();
                    if (ctx.isWholeLineLoopEnd()) {
                        // 单独的数组尾，这一行不需要作为模板处理
                    }
                    else {
                        stanzaBuf.append(StringUtils.substringBefore(line, Constants.TEMPLATE_LOOP_END));
                    }

                    if (ctx.isInLoop()) {
                        String rendered = processLoop(stanzaBuf.toString(), (LoopMatrix) dataHandler.getDataModel());
                        ctx.appendToBuffer(rendered);
                        ctx.popLoopState();
                        dataHandler.popDataModel();
                    }
                }
                else if (ctx.isStartLogic()) {
                    log.info("    entering logic");
                    if (ctx.isInLoop()) {
                        ctx.appendToBuffer(line).append(config.getInputLineBreaker());
                    }
                    else {
                        String logicKey = ctx.getLogicKey();
                        ctx.pushLogic(dataHandler.isLogicalTrue(logicKey));
                        log.debug("    logic value: " + ctx.isLogicTrue());
                    }
                }

                // 无状态改变
                else {
                    log.debug("    ...");
                    if (ctx.isInLoop()) {
                        log.debug("    in loop");
                        ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
                    }
                    else {
                        processLine(line, dataHandler, ctx);
                    }
                    continue;// 等待下一行一起处理
                }

                // 特殊处理文末
                if (i == lines.length - 1) {
                    log.info("reach to the end");
                    int start = ctx.getBuffer().length() - config.getOutputLineBreaker().length();
                    int end = ctx.getBuffer().length();
                    ctx.deleteInBuffer(start, end);
                }
            }

        }
        return ctx.popBuffer().toString();
    }

    private String processLoop(String templateStanza, LoopMatrix loopMatrix) {
        if (StringUtils.isBlank(templateStanza)) {
            throw new RuntimeException("Template stanza is empty");
        }
        StringBuilder outBuf = new StringBuilder();
        if (loopMatrix == null || loopMatrix.getMatrix().isEmpty()) {
            // 没有提供参数值，直接输出模板
            outBuf.append(templateStanza).append(config.getOutputLineBreaker());
        }
        else {
            TemplateEngine2 subEngine = new TemplateEngine2();
            for (Map<String, Object> matrix : loopMatrix.getMatrix()) {
                subEngine.setTemplate(templateStanza);
                String rendered = subEngine.process(new StackDataModelHandler(matrix));
                outBuf.append(rendered);
            }
        }
        return outBuf.toString();
    }

    /**
     * 处理简单的一行
     *
     * @param line
     * @param dataHandler
     * @param ctx
     */
    private void processLine(String line, DataModelHandler dataHandler, RenderContext ctx) {
        if (StringUtils.isBlank(line)) {
            // 处理空行
            ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
            return;
        }
        String[] keys = StringUtils.substringsBetween(line, "${", "}");
        // 没有参数，原样不动的返回一行
        if (keys == null || keys.length == 0) {
            log.warn("    No place holders for this line.");
            ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
        }
        else {
            if (ctx.isLogicFalse()) {
                return;
            }
            else {
                // 渲染这一行
                log.info("    String params: " + StringUtils.join(keys, ","));
                List<String> values = dataHandler.onLine(keys);
                String retLine = TextUtils.replaceWith(line, keys, values.toArray(new String[0]));
                log.info("    Render: ");
                log.data(retLine);
                ctx.appendToBuffer(retLine).append(config.getOutputLineBreaker());
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
