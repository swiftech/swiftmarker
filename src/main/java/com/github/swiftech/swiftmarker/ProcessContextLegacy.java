package com.github.swiftech.swiftmarker;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * 全局处理上下文
 * 功能：
 * 信息收集
 *
 * @author swiftech 2019-03-16
 **/
public class ProcessContextLegacy {

    /**
     * 全局信息
     */
    private List<String> messages = new LinkedList<>();

    /**
     * 分组名称 -> 信息集合
     */
    private TreeMap<String, List<String>> groupMessages = new TreeMap<>();
    // 指向当前的分组，适用于某些变量域内没有分组名的情况
    private String currentGroup = null;

    public void addMessage(String message) {
        this.messages.add(message);
    }

    /**
     * 添加信息至当前（最后一个）分组
     * @param message
     */
    public void addMessageToCurrentGroup(String message) {
        this.addGroupMessage(currentGroup, message);
    }

    /**
     * 添加一个分组
     *
     * @param groupName
     */
    public void addGroup(String groupName) {
        if (!groupMessages.containsKey(groupName)) {
            groupMessages.put(groupName, null);
            currentGroup = groupName;
        }
    }

    /**
     * 将信息添加到分组 groupName 中，如果分组不存在，则创建一个
     *
     * @param groupName 分组名称
     * @param message
     */
    public void addGroupMessage(String groupName, String message) {
        if (StringUtils.isBlank(groupName)) {
            return;
        }
        List<String> msgs = groupMessages.get(groupName);
        if (msgs == null) {
            msgs = new LinkedList<>();
            groupMessages.put(groupName, msgs);
            currentGroup = groupName;
        }
        msgs.add(message);
    }

    /**
     * 格式化输出所有消息至标准输入输出
     */
    public void printAllMessages() {
        System.out.println("  ========= Results ========  ");
        for (String msgs : messages) {
            System.out.println(msgs);
        }
        for (String groupName : groupMessages.keySet()) {
            List<String> msgs = groupMessages.get(groupName);
            System.out.printf("%s%s%n", groupName, msgs == null || msgs.isEmpty() ? " OK" : " has errors: ");
            if (msgs != null) {
                for (String msg : msgs) {
                    System.out.println("  " + msg);
                }
            }
        }
        System.out.println("  ========= Results ========  ");
    }


    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

}
