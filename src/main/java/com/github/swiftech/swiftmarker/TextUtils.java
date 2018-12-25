package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

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
     * @return
     */
    public static String replaceWith(String src, String[] keys, String[] values) {
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
            // 替换表达式，如果没有值，保留表达式
            if (!StringUtils.isBlank(value)) {
                ret = StringUtils.replace(ret, "${" + key + "}", value);
            }
        }
        return ret;
    }
}
