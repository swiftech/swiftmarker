package com.github.swiftech;

import com.github.swiftech.swiftmarker.TemplateParser;
import org.junit.Test;

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
        parser.parse(String.valueOf(Thread.currentThread().getId()), "how are you?..?[logic]xxx?[]");
    }
}
