package com.github.swiftech.swiftmarker.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech
 */
class LogicalValueTest {

    @Test
    void evaluate() {
        String exp = "s";
        Assertions.assertTrue(new LogicalValue(exp).evaluate((s) -> "Y"));
        Assertions.assertFalse(new LogicalValue(exp).evaluate((s) -> "N"));
        exp = "!s";
        Assertions.assertFalse(new LogicalValue(exp).evaluate((s) -> "Y"));
        Assertions.assertTrue(new LogicalValue(exp).evaluate((s) -> null));
    }
}