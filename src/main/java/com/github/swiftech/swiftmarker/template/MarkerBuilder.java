package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 * @see Marker
 * @since 3.0
 */
public class MarkerBuilder {
    private String value;
    private boolean isLogicBegin;
    private boolean isLogicEnd;
    private boolean isLoopBegin;
    private boolean isLoopEnd;
    private boolean isVar;
    private boolean isAvailable = true;

    public MarkerBuilder expression(String value) {
        this.value = value;
        return this;
    }

    public MarkerBuilder isLogicBegin(boolean isLogicBegin) {
        this.isLogicBegin = isLogicBegin;
        return this;
    }

    public MarkerBuilder isLogicEnd(boolean isLogicEnd) {
        this.isLogicEnd = isLogicEnd;
        return this;
    }

    public MarkerBuilder isLoopBegin(boolean isLoopBegin) {
        this.isLoopBegin = isLoopBegin;
        return this;
    }

    public MarkerBuilder isLoopEnd(boolean isLoopEnd) {
        this.isLoopEnd = isLoopEnd;
        return this;
    }

    public MarkerBuilder isVar(boolean isVar) {
        this.isVar = isVar;
        return this;
    }

    public MarkerBuilder isAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
        return this;
    }

    public Marker createDirective() {
        return new Marker(value, isLogicBegin, isLogicEnd, isLoopBegin, isLoopEnd, isVar, isAvailable);
    }
}