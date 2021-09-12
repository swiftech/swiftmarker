package com.github.swiftech.swiftmarker.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

/**
 * @author swiftech
 */
class CompoundLogicalOperationTest {

    static Function<String, Object> generator = s -> {
        if ("num".equals(s)) {
            return 10;
        }
        else if ("str".equals(s)) {
            return "foo";
        }
        else if ("bool".equals(s)) {
            return Boolean.TRUE;
        }
        else {
            return null;
        }
    };

    @Test
    void evaluateBasic() {
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num < 0").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("str = 'foo'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("str = 'bar'").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("bool").evaluate(generator));
    }

    @Test
    void evaluateOr() {

        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 | str = 'foo'").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 | str = 'bar'").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("num < 0 | str = 'foo'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num < 0 | str = 'bar'").evaluate(generator));
    }

    @Test
    void evaluateAnd() {
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 & str = 'foo'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num > 0 & str = 'bar'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num < 0 & str = 'foo'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num < 0 & str = 'bar'").evaluate(generator));
    }

    @Test
    void evaluateAndInOr() {
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 & num < 99 | str = 'foo'").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 & num < 99 | str = 'bar'").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 & num < 2 | str = 'foo'").evaluate(generator));
        Assertions.assertTrue(new CompoundLogicalOperation("num > 100 & num < 99 | str = 'foo'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num > 0 & num < 2 | str = 'bar'").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num > 100 & num < 99 | str = 'bar'").evaluate(generator));
    }

    @Test
    void evaluateComplex() {
        Assertions.assertTrue(new CompoundLogicalOperation("num > 0 & bool").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num > 0 & !bool").evaluate(generator));
        Assertions.assertFalse(new CompoundLogicalOperation("num < 0 & bool").evaluate(generator));
    }

}