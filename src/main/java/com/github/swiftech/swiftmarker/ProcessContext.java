package com.github.swiftech.swiftmarker;

import com.github.swiftech.swiftmarker.model.Message;
import com.github.swiftech.swiftmarker.model.TextMessage;
import com.github.swiftech.swiftmarker.model.MessageGroup;
import org.apache.commons.lang3.StringUtils;

/**
 * 全局处理上下文
 * 功能：
 * 信息收集
 *
 * @author swiftech 2020-01
 **/
public class ProcessContext {

    private DataModelHelper modelHelper = new DataModelHelper();

    /**
     * 分组名称 -> 信息集合
     */
    private MessageGroup rootMessage = new MessageGroup("");
    // 指向当前的分组，适用于某些变量域内没有分组名的情况
    private MessageGroup currentGroup = rootMessage;

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
     * 添加一个分组
     *
     * @param groupPattern 用点分隔的
     */
    public void addGroup(String groupPattern) {
        try {
            currentGroup = getCreateGroup(groupPattern);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MessageGroup getCreateGroup(String groupPattern) {
        MessageGroup groupFound = modelHelper.getValueRecursively(rootMessage, groupPattern, MessageGroup.class);
        if (groupFound == null) {
            if (!groupPattern.contains(".")) {
                // 已经是最后一个节点了
                groupFound = new MessageGroup(rootMessage.getLevel() + 1, groupPattern);
                rootMessage.put(groupPattern, groupFound);
                rootMessage.getMessages().add(groupFound);// 同时要放到列表中
            }
            else {
                String parentPattern = StringUtils.substringBeforeLast(groupPattern, ".");
                String groupName = StringUtils.substringAfterLast(groupPattern, ".");
                MessageGroup parentGroup = this.getCreateGroup(parentPattern); // Recursive
                groupFound = new MessageGroup(parentGroup.getLevel() + 1, groupName);
                parentGroup.put(groupName, groupFound);
                parentGroup.getMessages().add(groupFound); // 同时要放到列表中
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
        System.out.println("========= Results ========  ");
        this.visit(rootMessage, new Visitor<Message>() {
            @Override
            public void consume(Message message) {
                StringBuilder output = new StringBuilder();
                if (message instanceof TextMessage) {
                    output.append(StringUtils.repeat(" ", (message.getLevel()-1) * 2))
                            .append("|- ")
                            .append(((TextMessage) message).getContent());
                }
                else if (message instanceof MessageGroup) {
                    output.append(StringUtils.repeat(" ", (message.getLevel()-1) * 2))
                            .append("|- ")
                            .append(((MessageGroup) message).getGroupName());
                }
                System.out.println(output.toString());
            }
        });
        System.out.println("========= Results ========  ");
    }

    public void visit(MessageGroup node, Visitor<Message> visitor) {

        for (Message message : node.getMessages()) {
            if (message == null) {
                continue;
            }
            if (message instanceof TextMessage) {
                visitor.consume(message);
            }
            else if (message instanceof MessageGroup) {
                visitor.consume(message);
                this.visit((MessageGroup) message, visitor);
            }
            else {
                throw new RuntimeException("Not supported message type");
            }
        }
    }

    public interface Visitor<T> {
        //        void visit(String key, T t);
        void consume(T t);
    }

}
