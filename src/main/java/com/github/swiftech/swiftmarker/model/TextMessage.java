package com.github.swiftech.swiftmarker.model;

/**
 * @author swiftech
 */
public class TextMessage implements Message {

    int level;

    String content;

    public TextMessage() {
    }

    public TextMessage(String content) {
        this.content = content;
    }

    public TextMessage(int level, String content) {
        this.level = level;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "TextMessage{" +
                "level=" + level +
                ", content='" + content + '\'' +
                '}';
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }
}
