package com.github.swiftech.swiftmarker.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author swiftech
 */
public class MessageGroup extends HashMap<String, MessageGroup> implements Message {

    private int level = 0; // root level is 0

    /**
     * 消息总数（含所有层级）
     */
    private int totalCount = 0;

    /**
     * 强制显示
     */
    private boolean mandatory;

    /**
     * 消息组名称
     */
    private String groupName;

    /**
     * 消息列表
     */
    private List<Message> messages = new LinkedList<>();

    private MessageGroup parentGroup;

    public MessageGroup(String groupName) {
        this.groupName = groupName;
    }

    public MessageGroup(int level, String groupName) {
        this.level = level;
        this.groupName = groupName;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
        if (parentGroup != null) {
            parentGroup.setMandatory(mandatory);
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public MessageGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(MessageGroup parentGroup) {
        this.parentGroup = parentGroup;
    }

    public void addMessage(String msg) {
        this.messages.add(new TextMessage(this.level + 1, msg));
        this.increase();
    }

    /**
     * 递增自己的计数并递归向上更新父分组的计数
     */
    protected void increase() {
        totalCount++;
        if (parentGroup != null) {
            parentGroup.increase();
        }
    }


}
