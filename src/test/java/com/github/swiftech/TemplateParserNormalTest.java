package com.github.swiftech;

import com.github.swiftech.swiftmarker.TemplateParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * @author allen
 */
public class TemplateParserNormalTest {

    TemplateParser parser = new TemplateParser();

    @Test
    public void testVar() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "${var}");
    }

    @Test
    public void testLoop() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "$[loop]${var}xxx$[]");
    }

    @Test
    public void testLoop2(){
        parser.parse(String.valueOf(Thread.currentThread().getId()), "$[loop1]${var1}xxx$[]$[loop2]yyy${var2}$[]");
    }

    @Test
    public void testLogic() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "?{logic}${var}xxx?{}");
    }

    @Test
    public void testLoopNest() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "$[loop1]$[loop2]${var}xxx$[]$[]");
    }

    @Test
    public void testLogicNest() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "?{logic1}?{logic2}${var}xxx?{}?{}");
    }

    @Test
    public void testLogicLoopNest() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "?{logic}$[loop]${var}xxx$[]?{}");
    }

    @Test
    public void testLoopLogicNest() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "$[loop]?{logic}${var}xxx?{}$[]");
    }

    @Test
    public void testLoopLogicNestMultiLines() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "$[loop]\n?{logic}\n${var}\nxxx\n?{}\n$[]");
    }

    @Test
    public void testLoopsMultiLines() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "$[loop1]\n" +
                "${var}\n" +
                "xxx\n" +
                "$[]\n" +
                "\n" +
                "$[loop2]\n" +
                "yyy\n" +
                "$[]");
    }

    @Test
    public void testEscape() {
        parser.parse(RandomStringUtils.random(10), "$\\{var}");
        parser.parse(RandomStringUtils.random(10), "$\\[loop]$\\[]");
        parser.parse(RandomStringUtils.random(10), "?\\{logic}?\\{}");
    }

}
