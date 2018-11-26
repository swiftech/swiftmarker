package com.github.swiftech.swiftmarker;

import java.util.List;
import java.util.Map;

/**
 * 模板的行处理器，处理从不同的数据模型中取值，让引擎可以根据模板定义的位置填充数据。
 * @author swiftech
 **/
public interface DataModelHandler {

    /**
     * 处理一行
     * @param keysInLine 行所有的 key
     * @return 行所有的 key 的取值
     */
    List<String> onLine(String[] keysInLine);

    /**
     * 处理多行
     * @param arrayKey 行数组的 key
     * @return 行数组的 key 的取值
     */
    List<Map<String, String>> onLines(String arrayKey);
}
