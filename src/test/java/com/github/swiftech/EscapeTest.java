package com.github.swiftech;

import com.github.swiftech.swiftmarker.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * allen
 */
public class EscapeTest extends BaseResourceTest{

    @Before
    public void setup() {
        log.setLevel(Logger.LEVEL_DEBUG);
        config.setDebugLevel(Logger.LEVEL_DEBUG);
        engine.setConfig(config);
    }

    @Test
    public void testEscape() {
        String s = super.runFromResourceAndAssert("simple/escape");
        log.data(s);
    }

}
