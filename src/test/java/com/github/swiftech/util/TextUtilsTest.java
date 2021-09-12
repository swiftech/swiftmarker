package com.github.swiftech.util;

import com.github.swiftech.swiftmarker.util.TextUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author swiftech 2019-03-31
 **/
public class TextUtilsTest {

    @Test
    public void testJoin() {
        List l = new ArrayList() {
            {
                add("aaa");
                add("bbb");
                add("ccc");
            }
        };
        String join = TextUtils.join(l, ",");
        Assertions.assertEquals("\"aaa\",\"bbb\",\"ccc\"", join);
    }
}
