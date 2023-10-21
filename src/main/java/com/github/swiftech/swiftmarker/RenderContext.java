package com.github.swiftech.swiftmarker;

import java.util.Stack;

/**
 * 模板引擎上下文，包括：渲染缓存，换行处理标记
 *
 * @author swiftech 2018-12-03
 **/
public class RenderContext {

    /**
     * Stack of render buffer,
     */
    private final Stack<StringBuilder> renderBufStack = new Stack<>();

    /**
     * For some specific situation, line break is need to be trimmed after one directive was processed.
     *
     */
    private boolean needMoreTrim;

    public RenderContext() {
    }

    public void init() {
        renderBufStack.push(new StringBuilder());
    }

    /**
     * 创建一个新的渲染缓存并推入堆栈
     *
     * @return
     */
    public StringBuilder createBuffer() {
        StringBuilder buf = new StringBuilder();
        renderBufStack.push(buf);
        return buf;
    }

    /**
     * 将字符串追加到堆栈顶的 buffer 中
     *
     * @param s
     * @return
     */
    public StringBuilder appendToBuffer(String s) {
        if (s == null) return null;
        StringBuilder buf = renderBufStack.peek();
        if (buf == null) {
            throw new IllegalStateException("Render buffer is empty");
        }
        buf.append(s);
        return buf;
    }

    /**
     * 获取堆栈顶的 buffer
     *
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
     * Trim the line break of the buffer on the top of stack.
     *
     * @return
     */
    public boolean trimTailLineBreak() {
        StringBuilder buf = getBuffer();
        if (buf == null || buf.isEmpty()) {
            return false;
        }
        if (buf.charAt(buf.length() - 1) == '\n') {
            buf.deleteCharAt(buf.length() - 1);
            return true;
        }
        return false;
    }

    public boolean trimHeadLineBreak() {
        StringBuilder buf = getBuffer();
        if (buf == null || buf.isEmpty()) {
            return false;
        }
        if (buf.charAt(0) == '\n') {
            buf.deleteCharAt(0);
            return true;
        }
        return false;
    }

    /**
     * 删除整个文本的结尾
     *
     * @param config
     */
    public void trimTail(Config config) {
        int start = this.getBuffer().length() - config.getOutputLineBreaker().length();
        int end = this.getBuffer().length();
        this.deleteInBuffer(start, end);
    }

    /**
     * 删除文本缓存中指定位置区间的字符
     *
     * @param startInclusive
     * @param endExclusive
     * @return
     */
    private StringBuilder deleteInBuffer(int startInclusive, int endExclusive) {
        StringBuilder buf = renderBufStack.peek();
        if (buf == null || buf.isEmpty()) {
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

    public boolean isNeedMoreTrim() {
        return needMoreTrim;
    }

    public void setNeedMoreTrim(boolean needMoreTrim) {
        this.needMoreTrim = needMoreTrim;
    }
}
