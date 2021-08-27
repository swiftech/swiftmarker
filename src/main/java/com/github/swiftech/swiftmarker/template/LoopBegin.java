package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public class LoopBegin extends NestableDirective implements Begin {


    public LoopBegin(String value) {
        super(value);
    }

    public LoopBegin(String value, boolean isAvailable) {
        super(value);
        super.isAvailable = isAvailable;
    }

}
