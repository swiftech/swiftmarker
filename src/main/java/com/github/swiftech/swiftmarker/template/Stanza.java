package com.github.swiftech.swiftmarker.template;

import org.apache.commons.lang3.StringUtils;

/**
 * @author swiftech
 * @since 3.0
 */
public class Stanza extends Directive {

    public Stanza(String value) {
        super(value);
    }

    public static boolean endsWithLineBreak(Directive directive) {
        if (directive instanceof Stanza) {
            return directive.getValue().endsWith("\n");
        }
        return false;
    }

    public static boolean startsWithLineBreak(Directive directive) {
        if (directive instanceof Stanza) {
            return directive.getValue().startsWith("\n");
        }
        return false;
    }

    public String cutHead() {
        String value = this.getValue();
        String newValue = StringUtils.substring(value, 1, value.length());
        if (!StringUtils.isEmpty(newValue)) {
            return newValue;
        }
        else {
            return null;
        }
    }

    public String cutTail() {
        String value = this.getValue();
        String newValue = StringUtils.substring(value, 0, value.length() - 1);
        if (!StringUtils.isEmpty(newValue)) {
            return newValue;
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Stanza{" +
                "previous=" + (previous == null ? "null" : previous.getClass().getSimpleName()) +
                ", next=" + (next == null ? "null" : next.getClass().getSimpleName()) +
                ", value='" + value + '\'' +
                '}';
    }
}
