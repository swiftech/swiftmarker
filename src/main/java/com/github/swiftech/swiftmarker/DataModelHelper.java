package com.github.swiftech.swiftmarker;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author swiftech 2018-06-22
 **/
public class DataModelHelper {

    /**
     * 递归从数据模型容器中获取key对应的值。如果是数组对象，会转换成集合（Iterable）
     *
     * @param container
     * @param key
     * @param retType
     * @param <T>
     * @return
     */
    public <T> T getValueRecursively(Object container, String key, Class<T> retType) {
        String[] keys = StringUtils.split(key, '.');
        return _getValueRecursively(container, keys, 0, retType);
    }

    private <T> T _getValueRecursively(Object jo, String[] keys, int i, Class<T> retType) {
        String key = keys[i];
        Object value = getValue(jo, key);
        if (value == null) {
            return null;
        }
        if (ObjectUtils.isPrimitive(value)) {
            if (i == keys.length - 1) {
                // 找到最终的值
                return (T) getAsString(value);
            }
            else {
                return null; // 没有找到，或者类型不匹配
            }
        }
        else if (value.getClass().isArray()) {
            return (T)Arrays.asList((Object[]) value);
        }
        else if (ObjectUtils.isIterableList(value)) {
            if (i == keys.length - 1) {
                return (T) value; // 返回集合本身
            }
            else {
                return null; // 没有找到，或者类型不匹配
            }
        }
        else if (ObjectUtils.isKeyValueObject(value)) {
            if (i < keys.length - 1) {
                return _getValueRecursively(value, keys, ++i, retType);
            }
            else {
                return (T) value;
            }
        }
        else {
            // 未知类型
            return null;
        }
    }

    /**
     * 把一个对象转换成字符串
     *
     * @param o
     * @return
     */
    private String getAsString(Object o) {
        if (o instanceof JsonPrimitive) {
            return ((JsonPrimitive) o).getAsString();
        }
        else {
            return o.toString();
        }
    }

    /**
     * 自动判断容器对象并取出 key 对应的值对象
     *
     * @param container
     * @param key
     * @return
     */
    private Object getValue(Object container, String key) {
        if (container instanceof Map) {
            if (!((Map) container).containsKey(key)) {
                return null;
            }
            return ((Map) container).get(key);
        }
        else if (container instanceof JsonObject) {
            if (!((JsonObject) container).has(key)) {
                return null;
            }
            return ((JsonObject) container).get(key);
        }
        else {
            try {
                return forceGetProperty(container, key);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public Collection<String> getAllFieldsName(Class clazz) {
        Set<String> ret = new HashSet<>();
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] declaredFields = superClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (!field.isSynthetic()) {
                    ret.add(field.getName());
                }
            }
        }
        return ret;
    }


    /**
     * 暴力获取对象变量值，忽略private、protected修饰符的限制。
     *
     * @param object       对象
     * @param propertyName 属性名称
     * @return 属性的值
     * @throws NoSuchFieldException 没有该字段时抛出
     */
    public static Object forceGetProperty(Object object, String propertyName) throws NoSuchFieldException {

        Field field = getDeclaredField(object, propertyName);

        boolean accessible = field.isAccessible();
        field.setAccessible(true);

        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            System.out.printf("Can't get %s.%s value%n", object.getClass().getName(), propertyName);
        }
        field.setAccessible(accessible);
        return result;
    }

    /**
     * 循环向上转型，获取对象声明的字段。
     *
     * @param object       对象
     * @param propertyName 属性名称
     * @return 字段对象
     * @throws NoSuchFieldException 没有该字段时抛出
     */
    public static Field getDeclaredField(Object object, String propertyName) throws NoSuchFieldException {
        return getDeclaredField(object.getClass(), propertyName);
    }

    /**
     * 循环向上转型，获取对象声明的字段。
     *
     * @param clazz        类
     * @param propertyName 属性名称
     * @return 字段对象
     * @throws NoSuchFieldException 没有该字段时抛出
     */
    public static Field getDeclaredField(Class clazz, String propertyName) throws NoSuchFieldException {
        for (Class superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(propertyName);
            } catch (NoSuchFieldException e) {
                // Field不在当前类定义，继续向上转型
            }
        }
        throw new NoSuchFieldException("No such field: " + clazz.getName() + '.' + propertyName);
    }
}
