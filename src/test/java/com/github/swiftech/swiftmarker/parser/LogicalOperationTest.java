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
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> "A"));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> "B"));
        exp = "a>'A'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> "AA"));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> "B"));
        exp = "a>='A'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> "AA"));
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> "B"));
        exp = "a!='A'";
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> "A"));
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> "B"));
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> null));
        exp = "!a='A'";
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> "A"));
    }

    @Test
    public void testCheckValueNumber() {
        String exp = "a=9";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> 9));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> 10));
        exp = "a>9";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> 10));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> 9));
        exp = "a>=9";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> 9));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> 8));
    }

    @Test
    public void testCheckValueEnum() {
        Assertions.assertTrue("FOO".equals(ValueEnum.FOO.toString()));
        String exp = "a='FOO'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> ValueEnum.FOO));
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> ValueEnum.BAR));
    }

    @Test
    public void testWong() {
        String exp = "a='FOO'";
        Assertions.assertFalse(new LogicalOperation(exp).evaluate((s) -> null));
        exp = "a!='FOO'";
        Assertions.assertTrue(new LogicalOperation(exp).evaluate((s) -> null));

    }
}