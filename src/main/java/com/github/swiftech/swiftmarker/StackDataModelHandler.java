package com.github.swiftech.swiftmarker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * @author swiftech 2018-06-22
 */
public class StackDataModelHandler implements DataModelHandler {

    private DataModelHelper dataModelHelper;

    // 循环的数据堆栈
    private Stack<Object> dataModelStack = new Stack<>();

    // 根数据模型（即全局的数据模型）
    private Object rootDataModel;

    public StackDataModelHandler(Object container) {
        this(container, container);
    }

    public StackDataModelHandler(Object container, Object rootDataModel) {
        if (container.getClass().isPrimitive()) {
            throw new IllegalArgumentException("DataModel can not be primitive");
        }
        this.dataModelStack.push(container);
        this.dataModelHelper = new DataModelHelper();
        this.rootDataModel = rootDataModel;
    }

    @Override
    public boolean isLogicalTrue(String key) {
        Object value = parseValueLocalOrGlobal(key);
        if (value == null) {
            return false;
        }
        else if (value instanceof String) {
            if ("yes".equalsIgnoreCase((String) value)
                    || "y".equalsIgnoreCase((String) value)) {
                return true;
            }
            else if ("no".equalsIgnoreCase((String) value)
                    || "n".equalsIgnoreCase((String) value)) {
                return false;
            }
            else {
                return StringUtils.isNotBlank((CharSequence) value);
            }
        }
        else if (value instanceof Number) {
            return ((Number) value).longValue() > 0;
        }
        else if (value instanceof Boolean) {
            return (Boolean) value;
        }
        else if (value instanceof Date) {
            return ((Date) value).getTime() > 0;
        }
        else if (value instanceof Calendar) {
            return ((Calendar) value).getTimeInMillis() > 0;
        }
        else if (value instanceof JsonPrimitive) {
            if (((JsonPrimitive) value).isBoolean()) {
                return ((JsonPrimitive) value).getAsBoolean();
            }
            else if (((JsonPrimitive) value).isNumber()) {
                return ((JsonPrimitive) value).getAsNumber().doubleValue() > 0;
            }
            else if (((JsonPrimitive) value).isString()) {
                return ((JsonPrimitive) value).getAsString().length() > 0;
            }
        }
        else if (value instanceof Collection) {
            return ((Collection) value).size() > 0;
        }
        else if (value instanceof JsonArray) {
            return ((JsonArray) value).size() > 0;
        }
        else if (value instanceof Map) {
            return ((Map) value).size() > 0;
        }
        else if (value instanceof JsonObject) {
            return ((JsonObject) value).size() > 0;
        }
        else if (value.getClass().isArray()) {
            return ((Object[]) value).length > 0;
        }
        return false;
    }

    @Override
    public boolean isLogicalTrue(Object container, String key) {
        throw new NotImplementedException();
    }

    @Override
    public List<String> onKeys(String[] keys) {
        List<String> ret = new ArrayList<>();
        for (String key : keys) {
            Object v = parseValueLocalOrGlobal(key);
            if (v == null) {
                ret.add(StringUtils.EMPTY);
            }
            else {
                ret.add(v.toString());
            }
        }
        return ret;
    }

    private Object parseValueLocalOrGlobal(String key) {
        Object v;
        if (key.startsWith(".")) {
            v = dataModelHelper.getValueRecursively(
                    getTopDataModel(),
                    StringUtils.substringAfter(key.trim(), ".").trim(),
                    Object.class);
        }
        else {
            v = dataModelHelper.getValueRecursively(rootDataModel, key, Object.class);
        }
        return v;
    }

    @Override
    public LoopMatrix onLoop(String loopKey) {
        List<Map<String, Object>> ret = new ArrayList<>();

        Iterable dataMatrix = dataModelHelper.getValueRecursively(getTopDataModel(), loopKey, Iterable.class);
        if (dataMatrix == null) {
            Logger.getInstance().warn("No collection or key-value object in the data model by key: " + loopKey);
            return new LoopMatrix(ret);
        }

        // 遍历数据模型中的数组（含多值）
        for (Object dataRow : dataMatrix) {
            Map<String, Object> m = new HashMap<>();
            // 数组成员是 JsonObject，
            if (dataRow instanceof JsonObject) {
                for (String k : ((JsonObject) dataRow).keySet()) {
                    // 从数组成员中获值
                    Object v = dataModelHelper.getValueRecursively(dataRow, k, Object.class);
                    if (v != null) {
                        m.put(k, v);
                    }
                }
            }
            else if (dataRow instanceof Map) {
                for (Object k : ((Map) dataRow).keySet()) {
                    Object v = dataModelHelper.getValueRecursively(dataRow, (String) k, Object.class);
                    m.put(k.toString(), v);
                }
            }
            else if (ObjectUtils.isIterableList(dataRow)) {
                int i = 0;
                for (Object v : ((Iterable) dataRow)) {
                    m.put(String.valueOf(i++), v);
                }
            }
            else if (dataRow.getClass().isArray()) {
                Object[] arow = (Object[]) dataRow;
                for (int i = 0; i < arow.length; i++) {
                    m.put(String.valueOf(i), arow[i]);
                }
            }
            else if (!ObjectUtils.isPrimitive(dataRow)) {
                // Bean对象处理
                Collection<String> allFieldsName = dataModelHelper.getAllFieldsName(dataRow.getClass());
                for (String k : allFieldsName) {
                    Object v = dataModelHelper.getValueRecursively(dataRow, k, Object.class);
                    m.put(k, v);
                }
            }
            ret.add(m);
        }
        return new LoopMatrix(ret);
    }

    public Object getTopDataModel() {
        if (!dataModelStack.empty()) {
            return dataModelStack.peek();
        }
        return null;
    }

    public Object getRootDataModel() {
        return rootDataModel;
    }

    public void pushDataModel(Object dataModel) {
        dataModelStack.push(dataModel);
    }

    public Object popDataModel() {
        return dataModelStack.pop();
    }


}
