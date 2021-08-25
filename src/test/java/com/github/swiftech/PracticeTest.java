package com.github.swiftech;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Allen 2019-08-23
 **/
public class PracticeTest extends BaseResourceTest {
    @Before
    public void setup() {
//        log.setLevel(Logger.LEVEL_DEBUG);
        engine.setConfig(config);
    }

    /**
     * 测试空循环
     */
    @Test
    public void testEntityField() {
        String rendered = super.runFromResourceAndAssert("practice/entity_field", "practice/entity_field");
        log.data(rendered);
    }


    @Test
    public void testLoopWithBrace() {
        String rendered = super.runFromResourceAndAssert("practice/loop_with_brace");
        log.data(rendered);
    }
}
