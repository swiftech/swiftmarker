package com.github.swiftech.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Allen 2018-11-25
 **/
public class UtilsTest {

    @Test
    public void testSplit() {
        String[] strs = StringUtils.splitPreserveAllTokens("aaa\nbbb\nccc\n\nddd\n\n\neee\n\n\n\nfff", "\n");
        for (String str : strs) {
            System.out.println("'" + str + "'");
        }
        strs = StringUtils.splitPreserveAllTokens("aaa\rbbb\rccc\r\rddd\r\r\reee\r\r\r\rfff", "\r");
        for (String str : strs) {
            System.out.println("'" + str + "'");
        }
        strs = StringUtils.splitByWholeSeparatorPreserveAllTokens("aaa\r\nbbb\r\nccc\r\n\r\nddd\r\n\r\n\r\neee\r\n\r\n\r\n\r\nfff", "\r\n");
        for (String str : strs) {
            System.out.println("'" + str + "'");
        }
    }

    @Test
    public void testUnixWithTail() {
        String[] strs = StringUtils.splitPreserveAllTokens("aaa\nbbb\nccc\n\nddd\n\n\neee\n\n\n\nfff\0", "\n\0");
        for (String str : strs) {
            System.out.println("'" + str + "'");
        }
        Assertions.assertEquals(13, strs.length);
    }

    @Test
    public void testUnix() {
        String[] strs = StringUtils.splitPreserveAllTokens("aaa\nbbb\nccc\n\nddd\n\n\neee\n\n\n\nfff", "\n");
        for (String str : strs) {
            System.out.println("'" + str + "'");
        }
        Assertions.assertEquals(12, strs.length);

    }

    @Test
    public void testClassicMac() {
        String[] strs = StringUtils.splitPreserveAllTokens("aaa\rbbb\rccc\r\rddd\r\r\reee\r\r\r\rfff", "\r");
        System.out.println(strs.length);
        for (String str : strs) {
            System.out.println(str);
        }
        Assertions.assertEquals(12, strs.length);
    }

    @Test
    public void testWindows() {
        String[] strs = StringUtils.splitByWholeSeparatorPreserveAllTokens("aaa\r\nbbb\r\nccc\r\n\r\nddd\r\n\r\n\r\neee\r\n\r\n\r\n\r\nfff", "\r\n");
        System.out.println(strs.length);
        for (String str : strs) {
            System.out.println(str);
        }
        Assertions.assertEquals(12, strs.length);
    }

}
