package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @author swiftech 2018-11-26
 **/
public class TextUtils {

    /**
     * 依次替换掉对应的 ${xxx} 表示的标记
     *
     * @param src
     * @param keys
     * @param values
     * @param renderExpressionIfNoValue true：如果值为null或者空则渲染表达式，false：不渲染表达式
     * @return 替换的结果
     */
    public static String replaceWith(String src, String[] keys, String[] values, boolean renderExpressionIfNoValue) {
        if (keys == null || values == null) {
            return src;
        }
        if (values.length < keys.length) {
            throw new RuntimeException("Not enough values for keys: " + StringUtils.join(values));
        }
        String ret = src;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = values[i];

            if (renderExpressionIfNoValue) {
                // 替换表达式，如果没有值，保留表达式
                if (StringUtils.isNotBlank(value)) {
                    ret = StringUtils.replace(ret, "${" + key + "}", value);
                }
                else {
                    // 不处理，保留原样
                }
            }
            else {
                if (StringUtils.isNotBlank(value)) {
                    ret = StringUtils.replace(ret, "${" + key + "}", value);
                }
                else {
                    ret = StringUtils.replace(ret, "${" + key + "}", StringUtils.EMPTY);
                }
            }
        }
        return ret;
    }

    /**
     * 合并字符串表达式
     *
     * @param collection<String>
     * @param separator
     * @return
     */
    public static String join(Collection<String> collection, String separator) {
        StringBuilder buf = new StringBuilder();
        for (String item : collection) {
            buf.append("\"").append(item).append("\"").append(separator);
        }
        return buf.delete(buf.length() - separator.length(), buf.length()).toString();
    }

    /**
     * 合并字符串表达式
     *
     * @param collection<String>
     * @param separator
     * @return
     */
    public static String join(String[] collection, String separator) {
        StringBuilder buf = new StringBuilder();
        for (String item : collection) {
            buf.append("\"").append(item).append("\"").append(separator);
        }
        return buf.delete(buf.length() - separator.length(), buf.length()).toString();
    }
}
