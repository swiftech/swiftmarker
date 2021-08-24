package com.github.swiftech.util;

import com.github.swiftech.swiftmarker.util.TextUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Allen 2019-03-31
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
        Assert.assertEquals("\"aaa\",\"bbb\",\"ccc\"", join);
    }
}
