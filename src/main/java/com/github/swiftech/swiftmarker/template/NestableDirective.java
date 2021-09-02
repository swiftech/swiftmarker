package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public abstract class NestableDirective extends Directive {

    /**
     * This is used for later processing.
     */
    protected boolean isAvailable = false;

    public NestableDirective(String value) {
        super(value);
    }

    /**
     * 循环有效并且前面和后面都有换行符
     *
     * @return
     */
    public boolean isAvailableAndWrappedWithLineBreak() {
        return isAvailable
                && (previous == null || previous.isEndsWithLineBreak())
                && (next == null || next.isStartsWithLineBreak());
    }

    /**
     * Line breaks exists before and after this directive.
     *
     * @return
     */
    public boolean isWrappedWithLineBreak() {
        return (previous == null || previous.isEndsWithLineBreak())
                && (next == null || next.isStartsWithLineBreak());
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isNotAvailable() {
        return !isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }


    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "isAvailable=" + isAvailable +
                ", value='" + value + '\'' +
                ", previous=" + (previous == null ? "null" : previous.getClass().getSimpleName()) +
                ", next=" + (next == null ? "null" : next.getClass().getSimpleName()) +
                '}';
    }
}
