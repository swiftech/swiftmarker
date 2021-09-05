package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public class LogicEnd extends NestableDirective implements End {

    public LogicEnd() {
        super(null);
    }

    @Override
    public String toExpression() {
        return "?{}";
    }

}
