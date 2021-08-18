package com.github.swiftech.swiftmarker;

import com.github.swiftech.swiftmarker.model.GroupPattern;
import com.github.swiftech.swiftmarker.model.Message;
import com.github.swiftech.swiftmarker.model.TextMessage;
import com.github.swiftech.swiftmarker.model.MessageGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * 全局处理上下文
 * 功能：
 * 信息收集
 * (JSON 格式不能排序，不能混合数据结构，所以必须自定义数据结构）
 *
 * @author swiftech 2020-01
 **/
public class ProcessContext {

    private DataModelHelper modelHelper = new DataModelHelper();

    /**
     * 分组名称 -> 信息集合
     */
    private MessageGroup rootGroup = new MessageGroup(StringUtils.EMPTY);
    // 指向当前的分组，适用于某些变量域内没有分组名的情况
    private MessageGroup currentGroup = rootGroup;

    /**
     * 添加信息至根分组
     *
     * @param message
     */
    public void addMessageToRootGroup(String message) {
        if (rootGroup != null) {
            rootGroup.addMessage(message);
        }
    }

    /**
     * 添加信息至当前（最后一个）分组
     *
     * @param message
     */
    public void addMessageToCurrentGroup(String message) {
        if (currentGroup != null) {
            currentGroup.addMessage(message);
        }
    }

    /**
     * 按照"."分隔的组名添加一个分组
     *
     * @param groupPattern 用点分隔的
     */
    public MessageGroup addGroup(String groupPattern) {
        try {
            currentGroup = getCreateGroup(groupPattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentGroup;
    }

    /**
     * 添加一个新的分组至当前的分组中，并把当前分组指向新分组
     *
     * @param groupName
     * @return
     */
    public MessageGroup addGroupToCurrentGroup(String groupName) {
        MessageGroup newGroup = new MessageGroup(currentGroup.getLevel() + 1, groupName);
        currentGroup.put(groupName, newGroup);
        currentGroup.getMessages().add(newGroup); // 同时要放到列表中
        newGroup.setParentGroup(currentGroup);
        currentGroup = newGroup;
        return newGroup;
    }

    /**
     *
     * @param groupName
     * @return
     */
    public MessageGroup addGroupAsSibling(String groupName) {
        MessageGroup parentGroup = currentGroup.getParentGroup();
        MessageGroup newGroup = new MessageGroup(parentGroup.getLevel() + 1, groupName);
        parentGroup.put(groupName, newGroup);
        parentGroup.getMessages().add(newGroup); // 同时要放到列表中
        newGroup.setParentGroup(parentGroup);
        currentGroup = newGroup;
        return newGroup;
    }

    private MessageGroup getCreateGroup(String groupPattern) {
        MessageGroup groupFound = modelHelper.getValueRecursively(rootGroup, groupPattern, MessageGroup.class);
        if (groupFound == null) {
            if (!groupPattern.contains(".")) {
                // 已经是最后一个节点了
                groupFound = new MessageGroup(rootGroup.getLevel() + 1, groupPattern);
                rootGroup.put(groupPattern, groupFound);
                rootGroup.getMessages().add(groupFound);// 同时要放到列表中
                groupFound.setParentGroup(rootGroup);
            }
            else {
                GroupPattern gp = new GroupPattern(groupPattern);
                MessageGroup parentGroup = this.getCreateGroup(gp.getParentPattern()); // Recursive
                groupFound = new MessageGroup(parentGroup.getLevel() + 1, gp.getGroupName());
                parentGroup.put(gp.getGroupName(), groupFound);
                parentGroup.getMessages().add(groupFound); // 同时要放到列表中
                groupFound.setParentGroup(parentGroup);
            }
        }
        return groupFound;
    }

    /**
     * 将信息添加到分组 groupPattern 中，如果分组不存在，则创建一个
     *
     * @param groupPattern 分组名称
     * @param message
     */
    public void addGroupMessage(String groupPattern, String message) {
        if (StringUtils.isBlank(groupPattern)) {
            throw new IllegalStateException("No valid group pattern: " + groupPattern);
        }
        MessageGroup subGroup = this.getCreateGroup(groupPattern);
        subGroup.addMessage(message);
    }

    /**
     * 格式化输出所有消息至标准输入输出
     */
    public void printAllMessages() {
        System.out.println("========= Processing Results ========  ");
        this.visit(rootGroup, new Visitor<Message>() {
            @Override
            public void consume(Message message) {
                StringBuilder output = new StringBuilder();
                if (message instanceof TextMessage) {
                    output.append(StringUtils.repeat(" ", (message.getLevel() - 1) * 2))
                            .append("|- ")
                            .append(((TextMessage) message).getContent());
                }
                else if (message instanceof MessageGroup) {
                    output.append(StringUtils.repeat(" ", (message.getLevel() - 1) * 2))
                            .append("|- ")
                            .append(((MessageGroup) message).getGroupName());
                }
                System.out.println(output.toString());
            }
        });
        System.out.println("========= Processing Results ========  ");
    }

    /**
     * 递归访问消息树，深度优先
     *
     * @param node
     * @param visitor
     * @return
     */
    public List<TextMessage> visit(MessageGroup node, Visitor<Message> visitor) {
        List<TextMessage> ret = new LinkedList<>();
        for (Message message : node.getMessages()) {
            if (message == null) {
                continue;
            }
            if (message instanceof TextMessage) {
                visitor.consume(message);
            }
            else if (message instanceof MessageGroup) {
                if (((MessageGroup) message).getTotalCount() > 0
                        || ((MessageGroup) message).isMandatory()) {
                    visitor.consume(message);
                }
                this.visit((MessageGroup) message, visitor); // recursive to next level
            }
            else {
                throw new RuntimeException("Not supported message type");
            }
        }
        return ret;
    }

    public interface Visitor<T> {
        void consume(T t);
    }

}
