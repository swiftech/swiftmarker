package com.github.swiftech.swiftmarker.template;

/**
 * @author swiftech
 */
public abstract class Directive {

    // use pointer to organize directives to be a chain.
    protected Directive previous;
    protected Directive next;

    protected String value;

    public Directive(String value) {
        this.value = value;
    }

    public static boolean isLoopDirective(Directive directive) {
        return directive instanceof LoopBegin || directive instanceof LoopEnd;
    }

    public static boolean isLogicDirective(Directive directive) {
        return directive instanceof LogicBegin || directive instanceof LogicEnd;
    }

    public static boolean isBeginDirective(Directive directive) {
        return directive instanceof LogicBegin || directive instanceof LoopBegin;
    }

    public static boolean isEndDirective(Directive directive) {
        return directive instanceof LogicEnd || directive instanceof LoopEnd;
    }

    /**
     * Whether the value is a line break,
     * generally only works for {@link Stanza}
     *
     * @return
     */
//    public boolean isLineBreak() {
//        return "\n".equals(value);
//    }
    public boolean isStartsWithLineBreak() {
        return value != null && value.startsWith("\n");
    }

    public boolean isEndsWithLineBreak() {
        return value != null && value.endsWith("\n");
    }

    public void forBreakLink() {
        if (this.previous != null) {
            this.previous.setNext(this.next);
        }
        if (this.next != null) {
            this.next.setPrevious(this.previous);
        }
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Directive getPrevious() {
        return previous;
    }

    public void setPrevious(Directive previous) {
        this.previous = previous;
    }

    public Directive getNext() {
        return next;
    }

    public void setNext(Directive next) {
        this.next = next;
    }

    public boolean isMarker() {
        return this instanceof Marker;
    }

    public boolean isStanza() {
        return this instanceof Stanza;
    }

    public String toExpression() {
        return value;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
