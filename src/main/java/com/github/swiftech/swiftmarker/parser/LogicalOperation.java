package com.github.swiftech.swiftmarker.parser;

import com.github.swiftech.swiftmarker.util.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Optional;

import static com.github.swiftech.swiftmarker.constant.Constants.*;

/**
 * @author swiftech
 * @since 3.1
 */
public class LogicalOperation {

    private final String expression;
    private String key;
    private String operator;
    private Object value;
    private boolean isValid = true;

    public LogicalOperation(String expression) {
        this.expression = expression;
        this.parseExpression();
    }

    private void parseExpression() {
        Optional<String> first = ALL_LOGIC_EXPS.stream().filter(expression::contains).findFirst();
        if (!first.isPresent()) {
            this.isValid = false;
            return;
        }
        this.operator = first.get();
        String[] split = StringUtils.split(this.expression, this.operator);
        if (split == null || split.length != 2) {
            this.isValid = false;
            return;
        }
        this.key = split[0].trim();
        String s2 = split[1].trim();
        if (TextUtils.isWrappedWith(s2.trim(), "'")) {
            this.value = StringUtils.substringBetween(s2.trim(), "'", "'");
        }
        else {
            if (StringUtils.isNumeric(s2.trim())) {
                this.value = Integer.parseInt(s2.trim());
            }
            else {
                this.isValid = false;
            }
        }
    }

    public boolean isValid() {
        return isValid;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Evaluate the actualValue by this operation.
     *
     * @param actualValue
     * @return
     */
    public boolean evaluate(Object actualValue) {
        if (!isValid || actualValue == null) {
            return false;
        }
        if (actualValue instanceof Number) {
            int actualV = (int) actualValue;
            int expectV = (int) this.value;
            switch (this.operator) {
                case LOGIC_EXP_EQ:
                    return this.value.equals(actualValue);
                case LOGIC_EXP_NE:
                    return !this.value.equals(actualValue);
                case LOGIC_EXP_GT:
                    return NumberUtils.compare(actualV, expectV) > 0;
                case LOGIC_EXP_LT:
                    return NumberUtils.compare(actualV, expectV) < 0;
                case LOGIC_EXP_GE:
                    return NumberUtils.compare(actualV, expectV) >= 0;
                case LOGIC_EXP_LE:
                    return NumberUtils.compare(actualV, expectV) <= 0;
            }
        }
        else {
            switch (this.operator) {
                case LOGIC_EXP_EQ:
                    return this.value.equals(actualValue.toString());
                case LOGIC_EXP_NE:
                    return !this.value.equals(actualValue.toString());
                case LOGIC_EXP_GT:
                    return actualValue.toString().length() > this.value.toString().length();
                case LOGIC_EXP_LT:
                    return actualValue.toString().length() < this.value.toString().length();
                case LOGIC_EXP_GE:
                    return actualValue.toString().length() >= this.value.toString().length();
                case LOGIC_EXP_LE:
                    return actualValue.toString().length() <= this.value.toString().length();
            }
        }
        return false;
    }

}
