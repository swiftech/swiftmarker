package com.github.swiftech;

import com.github.swiftech.swiftmarker.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * swiftech
 */
public class EscapeTest extends BaseResourceTest{

    @BeforeEach
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
