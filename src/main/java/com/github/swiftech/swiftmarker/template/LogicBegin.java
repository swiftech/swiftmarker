package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public class LogicBegin extends NestableDirective implements Begin {

    public LogicBegin(String value) {
        super(value);
    }

    @Override
    public String toExpression() {
        return String.format("?{%s}", value);
    }

}
