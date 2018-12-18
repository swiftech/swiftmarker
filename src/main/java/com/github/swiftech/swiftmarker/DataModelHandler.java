package com.github.swiftech.swiftmarker;

import java.util.List;

/**
 * 模板的行处理器，处理从不同的数据模型中取值，让引擎可以根据模板定义的位置填充数据。
 *
 * @author swiftech
 **/
public interface DataModelHandler {

    /**
     * 从根节点查找
     * @param key
     * @return
     */
    boolean isLogicalTrue(String key);

    /**
     * 从指定容器查找
     * @param container
     * @param key
     * @return
     */
    boolean isLogicalTrue(Object container, String key);

    /**
     * 处理一行
     *
     * @param keysInLine 行所有的 key
     * @return 行所有的 key 的取值
     */
    List<String> onLine(String[] keysInLine);

    /**
     * 处理多行
     *
     * @param loopKey 行数组的 key
     * @return 行数组的 key 的取值
     */
    LoopMatrix onLoop(String loopKey);

    /**
     * 获取栈顶的数据模型
     * @return
     */
    Object getDataModel();

    /**
     * 压入数据模型至栈顶
     * @param dataModel
     */
    void pushDataModel(Object dataModel);

    /**
     * 推出栈顶数据模型
     * @return
     */
    Object popDataModel();
}
