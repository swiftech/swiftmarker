package com.github.swiftech.swiftmarker;

import java.util.List;

/**
 * 模板的行处理器，处理从不同的数据模型中取值，让引擎可以根据模板定义的位置填充数据。
 *
 * @author swiftech
 **/
public interface DataModelHandler {

    /**
     * 从根节点查找，是否逻辑为 true（只判断值是否为真）
     * @param key
     * @return
     */
    boolean isLogicalTrue(String key);

    /**
     * 从根节点查找，是否逻辑为 true 或者 false（会判断是否存在 "!" 表达式来反转逻辑）
     * @param key
     * @return
     */
    boolean isLogicalTrueOrFalse(String key);

    /**
     * 从指定容器查找
     * @param container
     * @param key
     * @return
     */
    boolean isLogicalTrue(Object container, String key);

    /**
     * 当前是否处于空的循环中（栈顶的 LoopMatrix 为空）
     *
     * @return
     */
    boolean isInEmptyLoop();

    /**
     * 处理一段字符串中所有的表达式 key
     *
     * @param keys
     * @return 所有的 key 的取值
     */
    List<String> onKeys(String[] keys);

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
    Object getTopDataModel();

    /**
     * 获取根数据模型
     * @return
     */
    Object getRootDataModel();

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
