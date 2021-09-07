package com.github.swiftech;

import com.github.swiftech.swiftmarker.TemplateParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

/**
 * @author allen
 */
public class TemplateParseAbnormalTest {

    TemplateParser parser = new TemplateParser();


    @Test
    public void testWrong() {
        parser.parse(String.valueOf(Thread.currentThread().getId()), "?[logic]xxx?[]");
    }


    @Test
    public void testStanzaWithKeywords() {
        parser.parse(RandomStringUtils.random(10), "how are you?..?{logic}xxx?{}");
        parser.parse(RandomStringUtils.random(10), "how are you??{logic}xxx?{}");
        parser.parse(RandomStringUtils.random(10), "how are you?${var}xxx");

        parser.parse(RandomStringUtils.random(10), "how are you$..$[logic]xxx$[]");
        parser.parse(RandomStringUtils.random(10), "how are you$$[logic]xxx$[]");
        parser.parse(RandomStringUtils.random(10), "how are you$?{logic}xxx?{}");
    }
}
