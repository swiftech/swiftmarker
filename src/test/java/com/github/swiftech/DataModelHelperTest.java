package com.github.swiftech;

import com.github.swiftech.swiftmarker.DataModelHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Allen 2018-06-22
 **/
public class DataModelHelperTest {

    @Test
    public void testNormal() {
        JsonObject jo1 = new JsonObject();
        JsonObject jo2 = new JsonObject();
        jo2.add("k12", new JsonPrimitive("v12"));
        jo1.add("k1", jo2);

        DataModelHelper dataModelHelper = new DataModelHelper();
        String s = dataModelHelper.getValueRecursively(jo1, "k1.k12", String.class);
        System.out.println("结果：" + s);
        Assert.assertEquals("v12", s);
    }

}
