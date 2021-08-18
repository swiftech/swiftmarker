package com.github.swiftech.swiftmarker.model;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * 处理按照逗号分隔各级分组名称，例如 "foo.bar"，
 * 名称中含有 "." 符号，则转义符 '\'，例如 用 "foo\.bar.1\.2'" 表示
 *
 * @author swiftech
 */
public class GroupPattern {

    private String pattern;

    private String parentPattern;

    private String groupName;

    private final char SUBSTITUTE = 26;

    public GroupPattern(String pattern) {
        this.pattern = pattern.trim();
    }

    public String getParentPattern() {
        if (isBlank(parentPattern)) {
            String replaced = replace(pattern, "\\.", String.valueOf(SUBSTITUTE));
            if (replaced.contains(".")) {
                parentPattern = substringBeforeLast(replaced, ".");
                parentPattern = replace(parentPattern, String.valueOf(SUBSTITUTE), ".");
            }
            else {
                parentPattern = EMPTY;
            }
        }
        return parentPattern;
    }

    public String getGroupName() {
        if (isBlank(groupName)) {
            String replaced = replace(pattern, "\\.", String.valueOf(SUBSTITUTE));
            if (replaced.contains(".")) {
                groupName = substringAfterLast(replaced, ".");
                groupName = replace(groupName, String.valueOf(SUBSTITUTE), ".");
            }
            else {
                groupName = pattern;
            }
        }
        return groupName;
    }
}
