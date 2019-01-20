package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

import java.util.Stack;

/**
 * 模板引擎上下文，包括：
 *
 * @author Allen 2018-12-03
 **/
public class RenderContext {

    // 逻辑表达式堆栈
    private Stack<String> logicKeyStack = new Stack<>();

    // 循环表达式堆栈
    private Stack<String> loopKeyStack = new Stack<>();

    // 状态堆栈
    private Stack<State> stateStack = new Stack<>();

    // 渲染缓存堆栈
    private Stack<StringBuilder> renderBufStack = new Stack<>();

    // 当前行
    private String line;

    public RenderContext() {
    }

    public RenderContext(String line) {
        this.line = line;
    }

    public void init() {
        renderBufStack.push(new StringBuilder());
    }

    public boolean isStartLogic() {
        String logicKey = StringUtils.substringBetween(line, "?{", "}");
        if (StringUtils.isNotBlank(logicKey)) {
            logicKeyStack.push(logicKey);
            return true;
        }
        return false;
    }

    public String getLogicKey() {
        if (!logicKeyStack.isEmpty()) {
            return logicKeyStack.peek();
        }
        return null;
    }

    public boolean isOutLogic() {
        if (StringUtils.contains(line, Constants.EXP_LOGIC_END)) {
            return true;
        }
        return false;
    }

    public void pushLogic(boolean logic) {
        if (logic) {
            stateStack.push(State.newLogicTrue());
        }
        else {
            stateStack.push(State.newLogicFalse());
        }
    }

    public boolean isLogicTrue() {
        if (!stateStack.isEmpty()) {
            return stateStack.peek().isLogicTrue();
        }
        return false;
    }

    public boolean isLogicFalse() {
        if (!stateStack.isEmpty()) {
            return stateStack.peek().isLogicFalse();
        }
        return false;
    }

    public void popLogicState() {
        if (!stateStack.empty() && stateStack.peek().isLogic()) {
            stateStack.pop();
        }
    }


    /**
     * @return
     */
    public boolean isStartLoop() {
        String loopKey = StringUtils.substringBetween(line, "$[", "]");
        if (StringUtils.isNotBlank(loopKey)) {
            loopKeyStack.push(loopKey);
            stateStack.push(State.newLoop());
            return true;
        }
        return false;
    }

    /**
     * @return
     */
    public boolean isInLoop() {
        if (stateStack.isEmpty()) {
            return false;
        }
        return stateStack.peek().isLoop();
    }

    /**
     * @return
     */
    public boolean isOutLoop() {
        if (StringUtils.contains(line.trim(), Constants.EXP_LOOP_END)) {
            return true;
        }
        return false;
    }

    public void popLoopState() {
        if (!stateStack.empty() && stateStack.peek().isLoop()) {
            stateStack.pop();
        }
    }

    /**
     * 整行都是结尾（去空格）
     * @return
     */
    public boolean isWholeLineLoopEnd() {
        return Constants.EXP_LOOP_END.equals(line.trim());
    }


    public String getLoopKey() {
        if (!loopKeyStack.empty()) {
            return loopKeyStack.peek();
        }
        return null;
    }

    /**
     * 创建一个新的渲染缓存并推入堆栈
     * @return
     */
    public StringBuilder createBuffer() {
        StringBuilder buf = new StringBuilder();
        renderBufStack.push(buf);
        return buf;
    }

    /**
     * @param s
     * @return
     */
    public StringBuilder appendToBuffer(String s) {
        StringBuilder buf = renderBufStack.peek();
        if (buf == null) {
            throw new IllegalStateException("Render buffer is empty");
        }
        buf.append(s);
        return buf;
    }

    /**
     * @return
     */
    public StringBuilder getBuffer() {
        StringBuilder buf = renderBufStack.peek();
        if (buf == null) {
            throw new IllegalStateException("Render buffer is empty");
        }
        return buf;
    }

    /**
     * 删除整个文本的结尾
     * @param config
     */
    public void trimTail(Config config) {
        int start = this.getBuffer().length() - config.getOutputLineBreaker().length();
        int end = this.getBuffer().length();
        this.deleteInBuffer(start, end);
    }

    /**
     * 删除文本缓存中指定位置区间的字符
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    private StringBuilder deleteInBuffer(int startInclusive, int endExclusive) {
        StringBuilder buf = renderBufStack.peek();
        if (buf == null) {
            throw new IllegalStateException("Render buffer is empty");
        }
        buf.delete(startInclusive, endExclusive);
        return buf;
    }

    /**
     * @return
     */
    public StringBuilder popBuffer() {
        if (renderBufStack.empty()) {
            throw new IllegalStateException("Render buffer is empty");
        }
        return renderBufStack.pop();
    }


    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }


}
