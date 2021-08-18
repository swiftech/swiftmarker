package com.github.swiftech;

import com.github.swiftech.swiftmarker.GsonUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

/**
 * allen
 */
public class GsonUtilsTest {

    @Test
    public void test() {
        JsonObject parent = new JsonObject();
        GsonUtils.set(parent, "a", new JsonObject());
        GsonUtils.set(parent, "a.b", new JsonObject());
        GsonUtils.set(parent, "a.b.c", new JsonPrimitive("abc"));
        System.out.println(parent);
        System.out.println(GsonUtils.get(parent, "a"));
        System.out.println(GsonUtils.get(parent, "a.b"));
        System.out.println(GsonUtils.get(parent, "a.b.c"));
    }

    /**
     * {
     *     "hello":"world",
     *     "yes": {
     *
     *     }
     * }
     */
    @Test
    public void testWhole() {

    }
}
