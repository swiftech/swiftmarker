package com.github.swiftech.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * allen
 */
public class StringUtilsTest {

    @Test
    public void test() {
        String str = "${${${foobar}}}";

        String s = StringUtils.substringBetween(str, "${", "}");
        System.out.println(s);

        String[] strings = StringUtils.substringsBetween(str, "${", "}");
        System.out.println(StringUtils.join(strings));

    }
}
