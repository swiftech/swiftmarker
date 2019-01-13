package com.github.swiftech.swiftmarker;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @param <C>
 * @author swiftech 2018-06-22
 * @deprecated 只支持简单的数据处理
 */
public class DefaultDataModelHandler<C> implements DataModelHandler {

    private C container;

    private DataModelHelper dataModelHelper;

    public DefaultDataModelHandler(C container) {
        this.container = container;
        dataModelHelper = new DataModelHelper();
    }

    @Override
    public boolean isLogicalTrue(String key) {
        return isLogicalTrue(this.container, key);
    }

    @Override
    public boolean isLogicalTrue(Object container, String key) {
        Object value = dataModelHelper.getValueRecursively(container, key, Object.class);
        if (value == null) {
            return false;
        }
        else if (value instanceof String) {
            return StringUtils.isNotBlank((CharSequence) value);
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
        return false;
    }

    @Override
    public List<String> onKeys(String[] keysInLine) {
        List<String> ret = new ArrayList<>();
        for (String key : keysInLine) {
            Object v = dataModelHelper.getValueRecursively(container, key, Object.class);
            if (v != null) {
                ret.add(v.toString());
            }
        }
        return ret;
    }

    @Override
    public LoopMatrix onLoop(String loopKey) {
        List<Map<String, Object>> ret = new ArrayList<>();

        Iterable dataMatrix = dataModelHelper.getValueRecursively(container, loopKey, Iterable.class);
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
                        m.put(k, v.toString());
                    }
                }
            }
            else if (dataRow instanceof Map) {
                for (Object k : ((Map) dataRow).keySet()) {
                    String v = dataModelHelper.getValueRecursively(dataRow, (String) k, String.class);
                    m.put(k.toString(), v);
                }
            }
            else if (ObjectUtils.isIterableList(dataRow)) {
                int i = 0;
                for (Object v : ((Iterable) dataRow)) {
                    m.put(String.valueOf(i++), v.toString());
                }
            }
            else if (dataRow.getClass().isArray()) {
                Object[] arow = (Object[]) dataRow;
                for (int i = 0; i < arow.length; i++) {
                    m.put(String.valueOf(i), arow[i].toString());
                }
            }
            else if (!ObjectUtils.isPrimitive(dataRow)) {
                // Bean对象处理
                Collection<String> allFieldsName = dataModelHelper.getAllFieldsName(dataRow.getClass());
                for (String k : allFieldsName) {
                    String v = dataModelHelper.getValueRecursively(dataRow, k, String.class);
                    m.put(k, v);
                }
            }
            ret.add(m);
        }
        return new LoopMatrix(ret);
    }

    @Override
    public Object getTopDataModel() {
        return null;
    }

    @Override
    public Object getRootDataModel() {
        return null;
    }

    @Override
    public void pushDataModel(Object dataModel) {

    }

    @Override
    public Object popDataModel() {
        return null;
    }
}
