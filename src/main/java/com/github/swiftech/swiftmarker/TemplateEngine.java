/*
 * Copyright 2018-2020 Yuxing Wang.
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


import com.github.swiftech.swiftmarker.constant.Constants;
import com.github.swiftech.swiftmarker.template.*;
import com.github.swiftech.swiftmarker.util.TextUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
 *
 * @author swiftech
 */
public class TemplateEngine {

    private final Logger log = Logger.getInstance();

    private String template;

    private Config config = new Config();

    private List<Directive> directives;// all directives
    private final RenderContext renderContext = new RenderContext();

    public TemplateEngine() {
    }

    public TemplateEngine(String template) {
        this.template = template;
    }

    public TemplateEngine(String template, Config config) {
        this.template = template;
        this.config = config;
    }

    /**
     * 执行模板渲染处理
     *
     * @param dataModel
     * @return
     */
    public String process(Object dataModel, ProcessContext processContext) {
        return this.process(dataModel, dataModel, processContext);
    }


    public String process(Object dataModel, Object rootDataModel, ProcessContext processContext) {
        log.info("Start to prepare template");
        TemplateParser templateParser = new TemplateParser();
        this.directives = templateParser.parse(this.toString(), this.template);
        StackDataModelHandler dataHandler = new StackDataModelHandler(dataModel, rootDataModel, processContext);
        log.info("Start to process rendering");
        this.render(0, new Stack<>(), dataHandler, 0);
        return renderContext.popBuffer().toString();
    }


    /**
     * @param directiveCursor
     * @param directiveStack
     * @param dataModelHandler
     * @param level            递归层级，最上层是0，子递归需要在遇到循环结束的时候 break
     * @return 返回遇到循环结束的时候的指令位置（也就是上一层递归需要跳到哪个指令之后开始执行）
     */
    private int render(int directiveCursor, Stack<Directive> directiveStack, StackDataModelHandler dataModelHandler, int level) {
        log.debug(String.format("== render from %d ==", directiveCursor));
        renderContext.createBuffer();
        // 从给定的指令位置开始
        for (int i = directiveCursor; i < directives.size(); i++) {
            // check if logic false or loop available
            Directive directive = directives.get(i);
            if (directive instanceof LogicBegin) {
                debugDirective("logic begin ->", i, level);
                LogicBegin logicBegin = (LogicBegin) directive;
                if (isTopDirectiveAvailable(directiveStack)) {
                    boolean available = dataModelHandler.isLogicalTrueOrFalse(logicBegin.getValue());
                    debugDirective(available ? "available" : "not available", i, level);
                    logicBegin.setAvailable(available);// TODO
                    directiveStack.push(logicBegin);
                    // 处理前面的换行
                    if (logicBegin.isWrappedWithLineBreak()) {
                        debugDirective("trail last line break", i, level);
                        renderContext.trimTailLineBreak();
                    }
                }
                else {
                    printStack(directiveStack);
                    logicBegin.setAvailable(false);
                    directiveStack.push(logicBegin);
                }
            }
            else if (directive instanceof LogicEnd) {
                debugDirective("logic end", i, level);
                Directive last = directiveStack.peek();
                if (last instanceof LogicBegin) {
                    LogicEnd logicEnd = (LogicEnd) directive;
                    logicEnd.setAvailable(((LogicBegin) last).isAvailable());// TODO
                    directiveStack.pop();
                    if (logicEnd.isAvailable() && logicEnd.isWrappedWithLineBreak()) {
                        debugDirective("trail last line break", i, level);
                        renderContext.trimTailLineBreak();
                    }
                }
                else {
                    log.warn("NOT MATCHED LOGIC DIRECTIVE");
                    printStack(directiveStack);
                }
            }
            else if (directive instanceof LoopBegin) {
                debugDirective("loop begin ->", i, level);
                // 从当前模型中获取集合，将集合元素倒序压入堆栈
                if (isTopDirectiveAvailable(directiveStack)) {
                    debugDirective("trail last line break", i, level);
                    renderContext.trimTailLineBreak();// TODO
                    LoopBegin loopBegin = (LoopBegin) directive;
                    LoopMatrix loopMatrix = dataModelHandler.onLoop(loopBegin.getValue());
                    List<Map<String, Object>> matrix = loopMatrix.getMatrix();
                    if (matrix == null || matrix.isEmpty()) {
                        loopBegin.setAvailable(false);
                        directiveStack.push(loopBegin);
                    }
                    else {
                        for (int j = matrix.size() - 1; j >= 0; j--) {
                            Map<String, Object> subModel = matrix.get(j);
                            dataModelHandler.pushDataModel(subModel);
                        }
                        loopBegin.setAvailable(true);
                        directiveStack.push(loopBegin);
                        int consumedAt = i;
                        debugDirective(String.format("loop: %s", loopBegin.getValue()), i, level);
                        for (int j = 0; j < loopMatrix.getMatrix().size(); j++) {
                            // == 递归 ==
                            Stack<Directive> subStack = new Stack<>();
                            subStack.push(loopBegin); // for matching the loop end in sub-func
                            debugDirective(String.format("call %dth rendering recursively", j), i, level);
                            consumedAt = this.render(i + 1, subStack, dataModelHandler, level + 1);
                            //debugDirective(String.format("%dth rendering done", j), i, level);
                            String rendered = renderContext.popBuffer().toString();
                            renderContext.appendToBuffer(rendered);
                        }
                        directiveStack.pop();
                        i = consumedAt;// 由于递归消耗掉了指令，将当前指令位置置为递归处理后的指令位置 (下一个循环开始会+1)
                    }
                }
            }
            else if (directive instanceof LoopEnd) {
                debugDirective("<- loop end", i, level);
                LoopEnd loopEnd = (LoopEnd) directive;
                if (isTopDirectiveLoopBegin(directiveStack)) {
                    if (isTopDirectiveAvailable(directiveStack)) {
                        loopEnd.setAvailable(true);
                        log.debug("pop last model");
                        dataModelHandler.popDataModel();
                    }
                    directiveStack.pop();
                    if (loopEnd.isAvailable() && loopEnd.isWrappedWithLineBreak()) {
                        debugDirective("trim last line break", i, level);
                        renderContext.trimTailLineBreak();
                    }
                    debugDirective(String.format("== render end at %d ==", i), i, level);
                    if (level > 0) return i;// 此处告诉上一层到哪个指令是循环结束
                }
                else {
                    log.warn("NOT MATCHED LOOP DIRECTIVE");
                }
            }
            else if (directive instanceof Var) {
                if (isTopDirectiveAvailable(directiveStack)) {
                    String rendered = dataModelHandler.onKey(directive.getValue());
                    debugDirective(String.format("var replacement: %s = '%s'", directive.getValue(), rendered), i, level);
                    renderContext.appendToBuffer(rendered);
                }
            }
            else if (directive instanceof Stanza) {
                if (isTopDirectiveAvailable(directiveStack)) {
                    Stanza stanza = (Stanza) directive;
                    if (stanza.getValue() != null) {
                        debugDirective("append stanza --", i, level);
                        String export = stanza.getValue();
                        log.debug(String.format("  '%s'", export));
                        renderContext.appendToBuffer(export);
                    }
                }
            }
        }
        return 0;
    }

    private void debugDirective(String msg, int i, int level) {
        log.debug(String.format("[%2d] %s %s", i, StringUtils.repeat('-', level * 2), msg));
    }

    private void printStack(Stack<Directive> directiveStack) {
        directiveStack.forEach(directive -> System.out.printf("%s(%s),", directive.getClass().getSimpleName(), (directive instanceof NestableDirective ? ((NestableDirective) directive).isAvailable() : "")));
        System.out.println();
    }

    /**
     * @param directiveStack
     * @return true if not exist
     */
    private boolean isTopDirectiveAvailable(Stack<Directive> directiveStack) {
        if (!directiveStack.isEmpty()) {
            Directive lastDirective = directiveStack.peek();
            if (lastDirective instanceof NestableDirective) {
                return ((NestableDirective) lastDirective).isAvailable();
            }
        }
        return true;
    }

    private boolean isTopDirectiveLoopBegin(Stack<Directive> directiveStack) {
        if (!directiveStack.isEmpty()) {
            Directive lastDirective = directiveStack.peek();
            return lastDirective instanceof LoopBegin;
        }
        return false;
    }

    private boolean isTopDirectiveLogicBegin(Stack<Directive> directiveStack) {
        if (!directiveStack.isEmpty()) {
            Directive lastDirective = directiveStack.peek();
            return lastDirective instanceof LogicBegin;
        }
        return false;
    }


    /**
     * 执行模板渲染处理
     *
     * @param dataModel
     * @return
     */
    public String process_v1(Object dataModel, Object rootDataModel, ProcessContext processContext) {
        if (StringUtils.isBlank(template)) {
            throw new RuntimeException("Template not loaded");
        }

        StackDataModelHandler dataHandler = new StackDataModelHandler(dataModel, rootDataModel, processContext);

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
                log.debug("    found quitting logic");
                // 存在数据才渲染；没有数据，忽略渲染
                if (dataHandler.isInEmptyLoop()) {
                    continue;
                }
                if (ctx.isInLoop()) {
                    ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
                }
                else {
                    if (ctx.isStartLogic()) {
                        if (ctx.isLogicFalse()) {
                            log.trace("  skip for logic false");
                            continue;
                        }
                        log.debug("    found entering logic");
                        String logicKey = ctx.getLogicKey();
                        String expLogicStart = "?{" + logicKey + "}";
                        String pre = StringUtils.substringBefore(line, expLogicStart);
                        processGeneralExpressions(pre, false, dataHandler, ctx);
                        ctx.pushLogic(dataHandler.isLogicalTrueOrFalse(logicKey));
                        log.trace(String.format("    logic expression: %s = %s", logicKey, ctx.isLogicTrue()));
                        if (ctx.isLogicTrue()) {
                            //
                            String raw = StringUtils.substringBetween(line, expLogicStart, Constants.EXP_LOGIC_END);
                            ctx.appendToBuffer(replaceKeys(raw, dataHandler));
                        }
                        ctx.popLogicState();
                        String post = StringUtils.substringAfter(line, Constants.EXP_LOGIC_END);

                        if (StringUtils.contains(post, "?{")) {
                            // 暂时解决一行中多个逻辑表达式的方法，以后要重构
                            TemplateEngine subEngine = new TemplateEngine();
                            subEngine.setTemplate(post);
                            subEngine.setConfig(this.config);
                            String rendered = subEngine.process(
                                    dataHandler.getTopDataModel(), dataHandler.getRootDataModel(), processContext);
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
            // 不是逻辑出
            else {
                if (ctx.isLogicFalse()) {
                    log.trace("  skip for logic false");
                    continue;
                }

                // 处理循环表达式开始
                if (ctx.isStartLoop()) {
                    log.debug("    entering loop...");
                    String loopKey = ctx.getLoopKey();
                    log.trace("    loop key: " + loopKey);

                    LoopMatrix loopMatrix = dataHandler.onLoop(loopKey);
                    dataHandler.pushDataModel(loopMatrix);
                    if (dataHandler.isInEmptyLoop()) {
                        ctx.createBuffer(); // 此处创建buffer只是为了避免后续处理抛出异常 TODO
//                        ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
                        continue;
                    }

                    String pre = StringUtils.substringBefore(line, "$[" + loopKey + "]");
                    processGeneralExpressions(pre, false, dataHandler, ctx);

                    // 直接结束了(inline)
                    if (ctx.isOutLoop()) {
                        log.trace("    end in line");
                        String subTemplate = StringUtils.substringBetween(line, "$[" + loopKey + "]", "$[]");
                        String rendered =
                                processLoop(subTemplate, loopMatrix, dataHandler.getRootDataModel(), processContext);
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
                        log.trace("    continue lines");
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
                    log.trace("    end loop");
                    StringBuilder stanzaBuf = ctx.popBuffer();
                    if (ctx.isWholeLineLoopEnd()) {
                        // 单独的数组尾，这一行不需要作为模板处理
                    }
                    else {
                        stanzaBuf.append(StringUtils.substringBefore(line, Constants.EXP_LOOP_END));
                    }

                    if (ctx.isInLoop()) {
                        // 存在数据才渲染；没有数据，忽略渲染
                        if (!dataHandler.isInEmptyLoop()) {
                            String rendered = this.processLoop(
                                    stanzaBuf.toString(),
                                    (LoopMatrix) dataHandler.getTopDataModel(),
                                    rootDataModel,
                                    processContext);
                            log.trace("    clear loop");
                            ctx.appendToBuffer(rendered);
                        }
                        ctx.popLoopState();
                        dataHandler.popDataModel();
                    }
                }
                else if (ctx.isStartLogic()) {
                    log.debug("    entering logic");
                    // 存在数据才渲染；没有数据，忽略渲染
                    if (dataHandler.isInEmptyLoop()) {
                        continue;
                    }
                    if (ctx.isInLoop()) {
                        ctx.appendToBuffer(line).append(config.getInputLineBreaker());
                    }
                    else {
                        String logicKey = ctx.getLogicKey();
                        ctx.pushLogic(dataHandler.isLogicalTrueOrFalse(logicKey));
                        log.trace(String.format("    logic expression: %s = %s", logicKey, ctx.isLogicTrue()));
                    }
                }
                // 无状态改变
                else {
                    // 存在数据才渲染；没有数据，忽略渲染
                    if (dataHandler.isInEmptyLoop()) {
                        continue;
                    }
                    if (ctx.isInLoop()) {
                        log.trace("    in loop, render while loop out");
                        ctx.appendToBuffer(line).append(config.getOutputLineBreaker());
                    }
                    else {
                        log.trace("    process general expressions");
                        processGeneralExpressions(line, true, dataHandler, ctx);
                    }
//                    continue;// 等待下一行一起处理
                }

                // 特殊处理文末
                if (i == lines.length - 1) {
                    log.debug("reach to the end");
                    ctx.trimTail(config); //如果模版为空或者只有标签，则会报错 TODO
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
    private String processLoop(String templateStanza, LoopMatrix loopMatrix, Object rootDataModel,
                               ProcessContext processContext) {
        if (StringUtils.isBlank(templateStanza)) {
            throw new RuntimeException("Template stanza is blank");
        }
        StringBuilder outBuf = new StringBuilder();
        if (loopMatrix == null || loopMatrix.getMatrix().isEmpty()) {
            log.debug("    output template stanza directly");
            // 没有提供参数值，直接输出模板(TODO 如果模板中存在全局变量则有问题）
            outBuf.append(templateStanza);//.append(config.getOutputLineBreaker());
        }
        else {
            log.debug("    recursively render sub template:");
            TemplateEngine subEngine = new TemplateEngine();
            subEngine.setConfig(this.config);
            for (Map<String, Object> matrix : loopMatrix.getMatrix()) {
                subEngine.setTemplate(templateStanza);
                String rendered = subEngine.process(matrix, rootDataModel, processContext);
                outBuf.append(rendered);
            }
        }
        return outBuf.toString();
    }

    /**
     * 处理模板片段的渲染，并加入到渲染缓存中
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
            log.trace("    No place holders for this line.");
            ctx.appendToBuffer(stanza);
            if (isEndOfLine) ctx.appendToBuffer(config.getOutputLineBreaker());
        }
        else {
            // 这个地方特殊处理一下转义符的情况（注意只能存在一层，否则会报错，而且中间不能有空格）
            boolean containsStarter = Arrays.stream(keys).anyMatch(s -> s.contains("${"));
            if (containsStarter) {
                String stub = StringUtils.substringAfter(keys[0], "${");
                String rendered = StringUtils.replace(stanza,
                        String.format("${${%s}}", stub),
                        String.format("${%s}", stub));
                ctx.appendToBuffer(rendered);
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
    }

    /**
     * 直接用 DataModelHandler 渲染模板片段
     *
     * @param stanza
     * @param dataHandler
     * @return
     */
    private String replaceKeys(String stanza, DataModelHandler dataHandler) {
        String[] keys = StringUtils.substringsBetween(stanza, "${", "}");
        if (keys == null || keys.length == 0) {
            log.debug("    No place holders for this line.");
            return stanza;
        }
        log.trace(String.format("    Param keys: [ %s ]", TextUtils.join(keys, ", ")));
        List<String> values = dataHandler.onKeys(keys);
        log.trace(String.format("    Values: [ %s ]", TextUtils.join(values, ", ")));
        String rendered = TextUtils.replaceWith(stanza, keys, values.toArray(new String[0]), config.isRenderExpressionIfValueIsBlank());
        log.trace("    Render: ");
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
