package com.github.swiftech.swiftmarker.model;

/**
 * @author swiftech
 */
public interface Message {

    /**
     * Which level in the message tree
     * @return
     */
    int getLevel();

    /**
     *
     * @param level
     */
    void setLevel(int level);
}
