package com.github.swiftech.swiftmarker.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Collection;
import java.util.Map;

/**
 * @author swiftech 2018-11-26
 **/
public class ObjectUtils {
    /**
     * 判断一个对象是否是 key-value 对象
     *
     * @param o
     * @return
     */
    public static boolean isKeyValueObject(Object o) {
        if (o == null) {
            return false;
        }
        return o instanceof JsonObject || o instanceof Map
                || !(isIterableList(o) || isPrimitive(o));
    }

    /**
     * 判断一个对象是否是数组对象
     *
     * @param o
     * @return
     */
    public static boolean isIterableList(Object o) {
        if (o == null) {
            return false;
        }
        return o instanceof JsonArray || o instanceof Collection;
    }

    /**
     * 判断一个对象是否是原生数据类型
     *
     * @param o
     * @return
     */
    public static boolean isPrimitive(Object o) {
        if (o == null) {
            return false;
        }
        return o instanceof JsonPrimitive
                || o instanceof String
                || o instanceof Integer
                || o instanceof Long
                || o instanceof Float
                || o instanceof Double
                || o instanceof CharSequence;
    }
}
