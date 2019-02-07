/*
 * Copyright 2018 Yuxing Wang.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.swiftech.swiftmarker;


import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 简易模板引擎
 * 规则：
 * ${xxx} 表示直接用对应的值进行替换
 * 如果用$[xxx]开头的表示用此行作为模板遍历数组
 * 注意：一行只能存在一个循环表达式，如果有多个，只处理第一个。
 * <p>
 * 1. 循环可以嵌套循环
 * 2. 逻辑判断可以嵌套逻辑判断
 * 3. 循环和逻辑判断可以互相嵌套（如果逻辑判断在循环中取值的话，则取子数据集中的数据）
 * </p>
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

    /**
     * 执行模板渲染处理
     * @param dataHandler
     * @return
     */
    public String process(DataModelHandler dataHandler) {
        if (StringUtils.isBlank(template)) {
            throw new RuntimeException("Template not loaded");
        }

        log.info("====  Template Engine Start to Process  ====");
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
                log.info("    found quitting logic");
                if (ctx.isInLoop()) {
                    ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
                }
                else {
                    if (ctx.isStartLogic()) {
                        log.info("    found entering logic");
                        String logicKey = ctx.getLogicKey();
                        String expLogicStart = "?{" + logicKey + "}";
                        String pre = StringUtils.substringBefore(line, expLogicStart);
                        processGeneralExpressions(pre, false, dataHandler, ctx);
                        ctx.pushLogic(dataHandler.isLogicalTrue(logicKey));
                        log.debug("    logic value: " + ctx.isLogicTrue());
                        if (ctx.isLogicTrue()) {
                            String raw = StringUtils.substringBetween(line, expLogicStart, Constants.EXP_LOGIC_END);
                            String retLine = replaceKeys(raw, dataHandler);
                            ctx.appendToBuffer(retLine);
                        }
                        ctx.popLogicState();
                        String post = StringUtils.substringAfter(line, Constants.EXP_LOGIC_END);

                        if (StringUtils.contains(post, "?{")) {
                            // 暂时解决一行中多个逻辑表达式的方法，以后要重构
                            TemplateEngine2 subEngine = new TemplateEngine2();
                            subEngine.setTemplate(post);
                            String rendered = subEngine.process(new StackDataModelHandler(dataHandler.getTopDataModel(), dataHandler.getRootDataModel()));
                            ctx.appendToBuffer(rendered);
                        }
                        else {
                            processGeneralExpressions(post, true, dataHandler, ctx);
                        }
                    }
                    else {
                        ctx.popLogicState();
                    }
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

                    LoopMatrix loopMatrix = dataHandler.onLoop(loopKey);
                    dataHandler.pushDataModel(loopMatrix);

                    String pre = StringUtils.substringBefore(line, "$[" + loopKey + "]");
                    processGeneralExpressions(pre, false, dataHandler, ctx);

                    // 直接结束了(inline)
                    if (ctx.isOutLoop()) {
                        log.debug("    end in line");
                        String subTemplate = StringUtils.substringBetween(line, "$[" + loopKey + "]", "$[]");
                        String rendered = processLoop(subTemplate, loopMatrix, dataHandler.getRootDataModel());
                        ctx.appendToBuffer(rendered);//.append(config.getOutputLineBreaker());
                        ctx.popLoopState();
                        dataHandler.popDataModel();
                        // 内嵌循环表达式后面的处理
                        String post = StringUtils.substringAfter(line, Constants.EXP_LOOP_END);
                        processGeneralExpressions(post, true, dataHandler, ctx);
                    }
                    else {
                        ctx.createBuffer(); // 新开一个渲染缓存?，貌似不需要，循环内部因为递归调用了引擎，其内部会初始化这个缓存的
                        String stanza = StringUtils.substringAfter(line, "$[" + loopKey + "]");
                        log.debug("    continue lines");
                        if (StringUtils.isBlank(stanza.trim())) {
                            // 循环表达式头单独出现，这一行不需要作为模板处理
                            continue;
                        }
                        else {
                            processGeneralExpressions(stanza, true, dataHandler, ctx);
//                            ctx.appendToBuffer(stanza).append(config.getOutputLineBreaker());
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
                        stanzaBuf.append(StringUtils.substringBefore(line, Constants.EXP_LOOP_END));
                    }

                    if (ctx.isInLoop()) {
                        String rendered = processLoop(
                                stanzaBuf.toString(),
                                (LoopMatrix) dataHandler.getTopDataModel(),
                                dataHandler.getRootDataModel());
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
                        processGeneralExpressions(line, true, dataHandler, ctx);
                    }
//                    continue;// 等待下一行一起处理
                }

                // 特殊处理文末
                if (i == lines.length - 1) {
                    log.info("reach to the end");
                    ctx.trimTail(config);
                }
            }
        }
        return ctx.popBuffer().toString();
    }

    /**
     * 处理模板中的循环表达式
     *
     * @param templateStanza 循环表达式内的模板片段
     * @param loopMatrix     循环的数据矩阵
     * @param rootDataModel  根数据模型，用于处理循环内的全局表达式
     * @return
     */
    private String processLoop(String templateStanza, LoopMatrix loopMatrix, Object rootDataModel) {
        if (StringUtils.isBlank(templateStanza)) {
            throw new RuntimeException("Template stanza is empty");
        }
        StringBuilder outBuf = new StringBuilder();
        if (loopMatrix == null || loopMatrix.getMatrix().isEmpty()) {
            // 没有提供参数值，直接输出模板(TODO 如果模板中存在全局变量则有问题）
            outBuf.append(templateStanza);//.append(config.getOutputLineBreaker());
        }
        else {
            TemplateEngine2 subEngine = new TemplateEngine2();
            for (Map<String, Object> matrix : loopMatrix.getMatrix()) {
                subEngine.setTemplate(templateStanza);
                String rendered = subEngine.process(new StackDataModelHandler(matrix, rootDataModel));
                outBuf.append(rendered);
            }
        }
        return outBuf.toString();
    }

    /**
     * 处理模板片段的渲染
     *
     * @param stanza
     * @param dataHandler
     * @param ctx
     */
    private void processGeneralExpressions(String stanza, boolean isEndOfLine, DataModelHandler dataHandler, RenderContext ctx) {
        if (StringUtils.isBlank(stanza)) {
            // 处理空行
            ctx.appendToBuffer(stanza);
            if (isEndOfLine) ctx.appendToBuffer(config.getOutputLineBreaker());
            return;
        }
        String[] keys = StringUtils.substringsBetween(stanza, "${", "}");
        // 没有参数，原样不动的返回一行
        if (keys == null || keys.length == 0) {
            log.warn("    No place holders for this line.");
            ctx.appendToBuffer(stanza);
            if (isEndOfLine) ctx.appendToBuffer(config.getOutputLineBreaker());
        }
        else {
            if (ctx.isLogicFalse()) {
                return;
            }
            else {
                String rendered = replaceKeys(stanza, dataHandler);
                ctx.appendToBuffer(rendered);
                if (isEndOfLine) ctx.appendToBuffer(config.getOutputLineBreaker());
            }
        }
    }

    /**
     * 直接用 DataModelHandler 渲染模板片段
     * @param stanza
     * @param dataHandler
     * @return
     */
    private String replaceKeys(String stanza, DataModelHandler dataHandler) {
        String[] keys = StringUtils.substringsBetween(stanza, "${", "}");
        if (keys == null || keys.length == 0) {
            log.warn("    No place holders for this line.");
            return stanza;
        }
        log.info(String.format("    String params: [ %s ]", StringUtils.join(keys, ", ")));
        List<String> values = dataHandler.onKeys(keys);
        log.info(String.format("    Values: [ %s ]", StringUtils.join(values, ", ")));
        String rendered = TextUtils.replaceWith(stanza, keys, values.toArray(new String[0]));
        log.info("    Render: ");
        log.data(rendered);
        return rendered;
    }


    public void setTemplate(String template) {
        this.template = template;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
