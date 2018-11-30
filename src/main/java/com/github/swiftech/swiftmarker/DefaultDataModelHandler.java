package com.github.swiftech.swiftmarker;

import com.google.gson.JsonObject;

import java.util.*;

/**
 * @param <C>
 * @author swiftech 2018-06-22
 */
public class DefaultDataModelHandler<C> implements DataModelHandler {

    private C container;

    private DataModelHelper dataModelHelper;

    public DefaultDataModelHandler(C container) {
//        if (!(container instanceof Map) || !(container instanceof Serializable)) {
//            throw new IllegalArgumentException("参数类型不支持");
//        }
        this.container = container;

        dataModelHelper = new DataModelHelper();
    }

    @Override
    public List<String> onLine(String[] keysInLine) {
        List<String> ret = new ArrayList<>();
        for (String key : keysInLine) {
            String v = dataModelHelper.getValueRecursively(container, key, String.class);
            ret.add(v);
        }
        return ret;
    }

    @Override
    public List<Map<String, String>> onLines(String arrayKey) {
        List<Map<String, String>> ret = new ArrayList<>();

        Iterable dataMatrix = dataModelHelper.getValueRecursively(container, arrayKey, Iterable.class);
        if (dataMatrix == null) {
            Logger.getInstance().warn("No collection or key-value object in the data model by key: " + arrayKey);
            return ret;
        }

        // 遍历数据模型中的数组（含多值）
        for (Object dataRow : dataMatrix) {
            Map<String, String> m = new HashMap<>();
            // 数组成员是 JsonObject，
            if (dataRow instanceof JsonObject) {
                for (String k : ((JsonObject) dataRow).keySet()) {
                    // 从数组成员中获值
                    String v = dataModelHelper.getValueRecursively(dataRow, k, String.class);
                    m.put(k, v);
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
        return ret;
    }
}
