package com.github.swiftech.swiftmarker.template;

/**
 * @author allen
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
}