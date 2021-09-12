package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContext;
import com.github.swiftech.swiftmarker.StackDataModelHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech
 */
public class StackDataModelHandlerTest {

    @Test
    public void testBoolean() {
        StackDataModelHandler handler = new StackDataModelHandler(new Object() {
            Object l1 = new Object() {
                Object l2 = new Object() {
                    boolean boolTrue = true;
                    Boolean boolFalse = false;
                };
            };
        }, new ProcessContext());
        Assertions.assertTrue(handler.isLogicalTrueOrFalse("l1.l2.boolTrue"));
        Assertions.assertFalse(handler.isLogicalTrueOrFalse("l1.l2.boolFalse"));
    }

    @Test
    public void testExpression() {
        StackDataModelHandler handler = new StackDataModelHandler(new Object() {
            Object l1 = new Object() {
                Object l2 = new Object() {
                    String a = "A";
                    int b = 9;
                };
            };
        }, new ProcessContext());
        Assertions.assertTrue(handler.isLogicalTrueOrFalse("l1.l2.a = 'A'"));
        Assertions.assertFalse(handler.isLogicalTrueOrFalse("l1.l2.b > 0"));
    }

}
