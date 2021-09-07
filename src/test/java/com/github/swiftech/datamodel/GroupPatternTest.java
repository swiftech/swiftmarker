package com.github.swiftech.datamodel;

import com.github.swiftech.swiftmarker.model.GroupPattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * allen
 */
public class GroupPatternTest {

    @Test
    public void testNoDot() {
        String pattern = "foobar";
        GroupPattern gp = new GroupPattern(pattern);
        Assertions.assertEquals("foobar", gp.getGroupName());
        Assertions.assertEquals("", gp.getParentPattern());
    }

    @Test
    public void testGetGroupName() {
        String pattern = "foo.2.bar";
        GroupPattern gp = new GroupPattern(pattern);
        Assertions.assertEquals("bar", gp.getGroupName());
        Assertions.assertEquals("foo.2", gp.getParentPattern());
    }

    @Test
    public void getGetWithQuotation() {
        String pattern = "foo\\.bar.1\\.2";
        GroupPattern gp = new GroupPattern(pattern);
        Assertions.assertEquals("1.2", gp.getGroupName());
        Assertions.assertEquals("foo.bar", gp.getParentPattern());
    }
}
