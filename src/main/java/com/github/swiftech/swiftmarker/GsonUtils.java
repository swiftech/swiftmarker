package com.github.swiftech.swiftmarker;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * 为 JSON 提供点分隔模式的存储和读取
 * 模式：
 * foo.bar: 表示 foo 对象的子对象 bar
 * foo[x].bar: 表示 foo.... TODO
 * 限制：目前只支持 JsonObject，不支持 JsonArray
 * allen
 *
 */
public class GsonUtils {

    /**
     * 设置模式为 pattern 的值，如果父节点不存在，则创建父节点
     *
     * @param node
     * @param pattern
     * @param element
     */
    public static void set(JsonObject node, String pattern, JsonElement element) {
        if (!pattern.contains(".")){
            node.add(pattern, element);
            return;
        }
        String parentPattern = StringUtils.substringBeforeLast(pattern, ".");
        String eleName = StringUtils.substringAfterLast(pattern, ".");
        JsonElement lastParent = _select(node, parentPattern.split("."), 0, true);
        if (lastParent != null && lastParent.isJsonObject()) {
            ((JsonObject) lastParent).add(eleName, element);
        }
        else {
            throw new RuntimeException();
        }
    }

    public static JsonElement get(JsonElement node, String pattern) {
        String[] tokens = StringUtils.split(pattern, ".");
        return _select(node, tokens, 0, false);
    }

    private static JsonElement _select(JsonElement curNode, String[] tokens, int idx, boolean create) {
        if (idx >= tokens.length) {
            return curNode;// Already last one, just return null.
        }
        String token = tokens[idx++];
        if (curNode instanceof JsonObject) {
            JsonElement child = ((JsonObject) curNode).get(token);
            if (child == null || child.isJsonNull()) {
                if (create) {
                    child = new JsonObject();// 不一定是Object哦
                    ((JsonObject) curNode).add(token, child);
                }
                else {
                    return null;
                }
            }
        }
        else {
            throw new RuntimeException("Not support JsonArray yet");
        }
        return _select(curNode, tokens, idx, create);
    }


    public static void visit(JsonElement node, Visitor<JsonElement> visitor) {
        if (node.isJsonObject()) {
            for (String key : ((JsonObject) node).keySet()) {
                JsonElement element = ((JsonObject) node).get(key);
                visitor.visit(key, element);
                visit(element, visitor);
            }
        }
        else if (node.isJsonArray()) {
            throw new RuntimeException();
        }
        else if (node.isJsonNull()) {
            throw new RuntimeException();
        }
    }

    public interface Visitor<T>  {
        void visit(String key, T t);
    }
}
