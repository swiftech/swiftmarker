package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public class Var extends Directive{

    public Var(String value) {
        super(value);
    }

    @Override
    public String toString() {
        return "Var{" +
                "previous=" + (previous == null ? "null" : previous.getClass().getSimpleName()) +
                ", next=" + (next == null ? "null" : next.getClass().getSimpleName()) +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public String toExpression() {
        return String.format("${%s}", value);
    }

}
