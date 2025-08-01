package com.github.swiftech.swiftmarker.parser;

import com.github.swiftech.swiftmarker.util.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Optional;
import java.util.function.Function;

import static com.github.swiftech.swiftmarker.constant.Constants.*;

/**
 * Compare value if they are numbers.
 * If not but String, compare the length of the String.
 * (Not support collection size yet)
 *
 * @author swiftech
 * @since 3.1
 */
public class LogicalOperation extends BaseOperation {

    private String key;
    private String operator;
    private Object value;
    private boolean isValid = true;

    public LogicalOperation(String expression) {
        super(expression);
        super.reverse = expression.startsWith("!");
        this.parseExpression();
    }

    private void parseExpression() {
        Optional<String> first = ALL_LOGIC_EXPS.stream().filter(expression::contains).findFirst();
        if (first.isEmpty()) {
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
     * @param valueGetter
     * @return
     */
    @Override
    public boolean evaluate(Function<String, Object> valueGetter) {
        if (!isValid) {
            return false;
        }
        if (super.reverse) {
            super.reverse = false;
            super.expression = super.expression.substring(1);
            return !this.evaluate(valueGetter);
        }
        else {
            Object actualValue = valueGetter.apply(this.getKey());
            if (actualValue == null) {
                return this.operator.equals(LOGIC_OP_NE); // 特殊处理 '!=' 的情况
            }
            if (actualValue instanceof Number) {
                int actualV = (int) actualValue;
                int expectV = (int) this.value;
                switch (this.operator) {
                    case LOGIC_OP_EQ:
                        return this.value.equals(actualValue);
                    case LOGIC_OP_NE:
                        return !this.value.equals(actualValue);
                    case LOGIC_OP_GT:
                        return NumberUtils.compare(actualV, expectV) > 0;
                    case LOGIC_OP_LT:
                        return NumberUtils.compare(actualV, expectV) < 0;
                    case LOGIC_OP_GE:
                        return NumberUtils.compare(actualV, expectV) >= 0;
                    case LOGIC_OP_LE:
                        return NumberUtils.compare(actualV, expectV) <= 0;
                }
            }
            else {
                switch (this.operator) {
                    case LOGIC_OP_EQ:
                        return this.value.equals(actualValue.toString());
                    case LOGIC_OP_NE:
                        return !this.value.equals(actualValue.toString());
                    case LOGIC_OP_GT:
                        return actualValue.toString().length() > this.value.toString().length();
                    case LOGIC_OP_LT:
                        return actualValue.toString().length() < this.value.toString().length();
                    case LOGIC_OP_GE:
                        return actualValue.toString().length() >= this.value.toString().length();
                    case LOGIC_OP_LE:
                        return actualValue.toString().length() <= this.value.toString().length();
                }
            }
            return false;
        }
    }

}
