package com.github.swiftech.swiftmarker.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author swiftech
 * @since 3.1
 */
public class CompoundLogicalOperation extends BaseOperation {

    private final Character operator;

    private List<BaseOperation> operations;

    public CompoundLogicalOperation(String expression) {
        super(expression);
        this.operator = '|';
        this.parseExpression();
    }

    private CompoundLogicalOperation(String expression, Character operator) {
        super(expression);
        this.operator = operator;
        this.parseExpression();
    }

    private void parseExpression() {
        String[] opsExps = StringUtils.split(this.expression, this.operator);
        System.out.println(this.operator + ": " + StringUtils.join(opsExps, ", "));
        this.operations = Arrays.stream(opsExps).map(e -> {
            if (e.contains("&")) {
                return new CompoundLogicalOperation(e, '&');
            }
            else if (StringUtils.containsAny(e, "=<>!")){
                return new LogicalOperation(e);
            }
            else {
                return new LogicalValue(e.trim());
            }
        }).collect(Collectors.toList());
    }

    @Override
    public boolean evaluate(Function<String, Object> valueGetter) {
        if ('|' == this.operator) {
            return this.operations.stream().map(logicalOperation -> logicalOperation.evaluate(valueGetter)).reduce(false, (b1, b2) -> b1 || b2);
        }
        else if ('&' == this.operator) {
            return this.operations.stream().map(logicalOperation -> logicalOperation.evaluate(valueGetter)).reduce(true, (b1, b2) -> b1 && b2);
        }
        else {
            throw new RuntimeException("Unknown operator");
        }
    }
}
