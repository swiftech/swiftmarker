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


import com.github.swiftech.swiftmarker.template.*;
import org.apache.commons.lang3.StringUtils;

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
        log.info("====  Template Engine Start to Process  ====");
        log.info("Config: ");
        log.info("  Output line breaker:" + (StringUtils.LF.equals(config.getOutputLineBreaker()) ? "LF" : "CRLF"));
        log.info("Template: ");
        log.data(template);
        TemplateParser templateParser = new TemplateParser();
        this.directives = templateParser.parse(this.toString(), this.template);
        if (this.directives == null || this.directives.isEmpty()) {
            throw new RuntimeException("Template is invalid");
        }
        StackDataModelHandler dataHandler = new StackDataModelHandler(dataModel, rootDataModel, processContext);
        log.info("== Start to process rendering ==");
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
        log.debug(String.format("render from %d", directiveCursor));
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
                    logicBegin.setAvailable(available);
                    directiveStack.push(logicBegin);
                    // 处理前面的换行
                    if (logicBegin.isWrappedWithLineBreak()) {
                        debugDirective("trim last line break", i, level);
                        boolean needTrimMore = !renderContext.trimTailLineBreak();
                        if (needTrimMore) {
                            log.debug("fail and need more trim later");
                            renderContext.setNeedMoreTrim(true); // 处理模版是以嵌套指令开始的情况
                        }
                    }
                }
                else {
                    //printStack(directiveStack);
                    logicBegin.setAvailable(false);
                    directiveStack.push(logicBegin);
                }
            }
            else if (directive instanceof LogicEnd) {
                debugDirective("logic end", i, level);
                Directive last = directiveStack.peek();
                if (last instanceof LogicBegin) {
                    LogicEnd logicEnd = (LogicEnd) directive;
                    logicEnd.setAvailable(((LogicBegin) last).isAvailable());
                    directiveStack.pop();
                    if (logicEnd.isAvailable() && logicEnd.isWrappedWithLineBreak()) {
                        debugDirective("trim last line break", i, level);
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
                    LoopBegin loopBegin = (LoopBegin) directive;
                    debugDirective("trim last line break", i, level);
                    boolean needTrimMore = !renderContext.trimTailLineBreak();
                    if (needTrimMore) {
                        log.debug("fail and need more trim later");
                        renderContext.setNeedMoreTrim(true); // 处理模版是以嵌套指令开始的情况
                    }
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
                        debugDirective("pop last model", i, level);
                        dataModelHandler.popDataModel();
                    }
                    directiveStack.pop();
                    if (loopEnd.isAvailable()) {
                        if (loopEnd.isWrappedWithLineBreak()) {
                            debugDirective("trim last line break", i, level);
                            renderContext.trimTailLineBreak();
                        }
                        debugDirective(String.format("== render end at %d ==", i), i, level);
                        if (level > 0) {
                            log.debug("quiting this level: " + level);
                            return i; // 子递归时及时退出，并且告诉上一层到哪个指令是循环结束
                        }
                    }
                }
                else {
                    log.warn("NOT MATCHED LOOP DIRECTIVE");
                    printStack(directiveStack);
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
                        debugDirective("append stanza ", i, level);
                        debugDirective(String.format("'%s'", stanza.getValue()), i, level);
                        renderContext.appendToBuffer(stanza.getValue());
                        this.trimMoreLineBreakAtHead(); // 处理模版是以嵌套指令开始的情况
                    }
                }
            }
        }
        return 0;
    }

    /**
     * 如果之前的指令处理标记为需要修剪（need trim)，那么把当前的 Stanza 头部的换行删掉
     */
    private void trimMoreLineBreakAtHead() {
        if (renderContext.isNeedMoreTrim()) {
            log.debug("trim last head line break ");
            renderContext.trimHeadLineBreak();
            renderContext.setNeedMoreTrim(false);
        }
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


    private void debugDirective(String msg, int i, int level) {
        log.debug(String.format("(%2d) %s %s", i, StringUtils.repeat('-', level * 2), msg));
    }

    private void printStack(Stack<Directive> directiveStack) {
        directiveStack.forEach(directive -> System.out.printf("%s(%s),", directive.getClass().getSimpleName(), (directive instanceof NestableDirective ? ((NestableDirective) directive).isAvailable() : "")));
        System.out.println();
    }


    public void setTemplate(String template) {
        this.template = template;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
