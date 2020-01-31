package com.github.swiftech.swiftmarker.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author swiftech
 */
public class MessageGroup extends HashMap<String, MessageGroup> implements Message{

    private int level = 0; // root level is 0

    /**
     * 消息组名称　
     */
    private String groupName;

    /**
     * 消息列表
     */
    private List<Message> messages = new LinkedList<>();

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

    public void addMessage(String msg) {
        this.messages.add(new TextMessage(this.level + 1, msg));
    }


}
