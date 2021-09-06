package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContext;
import com.github.swiftech.swiftmarker.StackDataModelHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author allen
 */
public class StackDataModelHandlerTest {

    @Test
    public void testBoolean() {
        StackDataModelHandler handler = new StackDataModelHandler(new Object(){
            Object l1 = new Object(){
                Object l2 = new Object(){
                    boolean boolTrue = true;
                    Boolean boolFalse = false;
                };
            };
        }, new ProcessContext());
        Assert.assertTrue(handler.isLogicalTrue("l1.l2.boolTrue"));
        Assert.assertFalse(handler.isLogicalTrue("l1.l2.boolFalse"));
    }
}
