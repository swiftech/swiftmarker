package com.github.swiftech.datamodel;

import com.github.swiftech.swiftmarker.model.GroupPattern;
import org.junit.Assert;
import org.junit.Test;

/**
 * allen
 */
public class GroupPatternTest {

    @Test
    public void testNoDot() {
        String pattern = "foobar";
        GroupPattern gp = new GroupPattern(pattern);
        Assert.assertEquals("foobar", gp.getGroupName());
        Assert.assertEquals("", gp.getParentPattern());
    }

    @Test
    public void testGetGroupName() {
        String pattern = "foo.2.bar";
        GroupPattern gp = new GroupPattern(pattern);
        Assert.assertEquals("bar", gp.getGroupName());
        Assert.assertEquals("foo.2", gp.getParentPattern());
    }

    @Test
    public void getGetWithQuotation() {
        String pattern = "foo\\.bar.1\\.2";
        GroupPattern gp = new GroupPattern(pattern);
        Assert.assertEquals("1.2", gp.getGroupName());
        Assert.assertEquals("foo.bar", gp.getParentPattern());
    }
}
