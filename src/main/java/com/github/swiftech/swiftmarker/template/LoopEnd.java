package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public class LoopEnd extends NestableDirective implements End {

    public LoopEnd() {
        super(null);
    }
    @Override
    public String toExpression() {
        return "$[]";
    }

}
