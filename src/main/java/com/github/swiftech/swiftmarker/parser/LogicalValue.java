package com.github.swiftech.swiftmarker.parser;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author swiftech
 * @since 3.1
 */
public class LogicalValue extends BaseOperation {


    public LogicalValue(String expression) {
        super(expression);
        this.parseExpression();
    }

    private void parseExpression() {
        super.reverse = this.expression.startsWith("!");
    }

    @Override
    public boolean evaluate(Function<String, Object> valueGetter) {
        if (super.reverse) {
            super.reverse = false;
            super.expression = super.expression.substring(1);
            return !evaluate(valueGetter); // call myself to reverse
        }
        else {
            Object value = valueGetter.apply(this.getExpression());// expression is just the key
            if (value == null) {
                return false;
            }
            else if (value instanceof String) {
                if ("yes".equalsIgnoreCase((String) value)
                        || "y".equalsIgnoreCase((String) value)) {
                    return true;
                }
                else if ("no".equalsIgnoreCase((String) value)
                        || "n".equalsIgnoreCase((String) value)) {
                    return false;
                }
                else {
                    return StringUtils.isNotBlank((CharSequence) value);
                }
            }
            else if (value instanceof Number) {
                return ((Number) value).longValue() > 0;
            }
            else if (value instanceof Boolean) {
                return (Boolean) value;
            }
            else if (value instanceof Date) {
                return ((Date) value).getTime() > 0;
            }
            else if (value instanceof Calendar) {
                return ((Calendar) value).getTimeInMillis() > 0;
            }
            else if (value instanceof JsonPrimitive) {
                if (((JsonPrimitive) value).isBoolean()) {
                    return ((JsonPrimitive) value).getAsBoolean();
                }
                else if (((JsonPrimitive) value).isNumber()) {
                    return ((JsonPrimitive) value).getAsNumber().doubleValue() > 0;
                }
                else if (((JsonPrimitive) value).isString()) {
                    return ((JsonPrimitive) value).getAsString().length() > 0;
                }
            }
            else if (value instanceof Collection) {
                return ((Collection) value).size() > 0;
            }
            else if (value instanceof JsonArray) {
                return ((JsonArray) value).size() > 0;
            }
            else if (value instanceof Map) {
                return ((Map) value).size() > 0;
            }
            else if (value instanceof JsonObject) {
                return ((JsonObject) value).size() > 0;
            }
            else if (value.getClass().isArray()) {
                return ((Object[]) value).length > 0;
            }
            return false;
        }
    }
}
