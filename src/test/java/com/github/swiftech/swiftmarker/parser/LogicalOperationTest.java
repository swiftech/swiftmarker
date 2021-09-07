package com.github.swiftech.swiftmarker.parser;

import com.github.swiftech.datamodel.ValueEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech
 */
public class LogicalOperationTest {

    @Test
    public void testCheckValueString() {
        String exp = "a='A'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate("A"));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate("B"));
        exp = "a>'A'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate("AA"));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate("B"));
        exp = "a>='A'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate("AA"));
        Assertions.assertTrue(new LogicalOperation(exp).evaluate("B"));
    }

    @Test
    public void testCheckValueNumber() {
        String exp = "a=9";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate(9));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate(10));
        exp = "a>9";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate(10));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate(9));
        exp = "a>=9";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate(9));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate(8));
    }

    @Test
    public void testCheckValueEnum() {
        Assertions.assertTrue("FOO".equals(ValueEnum.FOO.toString()));
        String exp = "a='FOO'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate(ValueEnum.FOO));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate(ValueEnum.BAR));
    }
}