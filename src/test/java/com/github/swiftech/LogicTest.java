package com.github.swiftech;

import com.github.swiftech.swiftmarker.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Allen 2018-11-30
 **/
public class LogicTest extends BaseResourceTest {

    @Before
    public void setup() {
        log.setLevel(Logger.LEVEL_DEBUG);
        engine.setConfig(config);
    }

    /**
     * 测试各种逻辑true
     */
    @Test
    public void testLogicBasicTrue() {
        String s = super.runFromResourceAndAssert("logic/basic");
        log.data(s);
    }

    /**
     * 测试各种逻辑false
     */
    @Test
    public void testLogicBasicFalse() {
        String s = super.runFromResourceAndAssert("logic/basic", "logic/basic_false", "logic/basic_false");
        log.data(s);
    }

    /**
     * 测试行内逻辑(True)
     */
    @Test
    public void testLogicTrueInLine() {
        String s = super.runFromResourceAndAssert("logic/inline", "logic/basic");
        log.data(s);
    }

    /**
     * 测试行内逻辑(False)
     */
    @Test
    public void testLogicFalseInLine() {
        String s = super.runFromResourceAndAssert("logic/inline", "logic/basic_false", "logic/inline_false");
        log.data(s);
    }

    @Test
    public void testLogicWithLoop() {
        String s = super.runFromResourceAndAssert("logic/with_loop");
        log.data(s);
    }

    @Test
    public void testLogicWithLoopInline() {
        String s = super.runFromResourceAndAssert("logic/with_loop_inline", "logic/with_loop", "logic/with_loop_inline");
        log.data(s);
    }

    /**
     * 测试2层逻辑
     */
    @Test
    public void testNestLogic2lTrue() {
        String rendered = runFromResourceAndAssert("logic/2l", "logic/2l_true", "logic/2l_true");
        log.data(rendered);

    }

    /**
     * 测试2层逻辑
     */
    @Test
    public void testNestLogic2lFalse() {
        String rendered = runFromResourceAndAssert("logic/2l", "logic/2l_false", "logic/2l_false");
        log.data(rendered);
    }


    /**
     * 测试复杂的多行模板渲染
     */
    @Test
    public void testLogicComplex() {
        String s = super.runFromResourceAndAssert("logic/complex");
        log.data(s);
    }

    @Test
    public void testLogicPractice() {
        String s = super.runFromResourceAndAssert("logic/practice");
    }

}
