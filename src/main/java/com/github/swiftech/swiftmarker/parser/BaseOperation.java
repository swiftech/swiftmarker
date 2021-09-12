package com.github.swiftech.swiftmarker.parser;

import java.util.function.Function;

/**
 * @author swiftech
 * @since 3.1
 */
public abstract class BaseOperation {

    protected String expression;

    protected boolean reverse = false;

    public BaseOperation(String expression) {
        this.expression = expression;
    }

    public abstract boolean evaluate(Function<String, Object> valueGetter);

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
