package com.github.swiftech.swiftmarker;

import com.github.swiftech.swiftmarker.parser.CompoundLogicalOperation;
import com.github.swiftech.swiftmarker.parser.LogicalValue;
import com.github.swiftech.swiftmarker.util.ObjectUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author swiftech 2018-06-22
 */
public class StackDataModelHandler implements DataModelHandler {

    private final DataModelHelper dataModelHelper;

    // 循环的数据堆栈
    private final Stack<Object> dataModelStack = new Stack<>();

    // 根数据模型（即全局的数据模型）
    private final Object rootDataModel;

    private final ProcessContext processContext;


    /**
     * @param container
     * @param processContext
     */
    public StackDataModelHandler(Object container, ProcessContext processContext) {
        this(container, container, processContext);
    }

    /**
     * @param container
     * @param rootDataModel
     * @param processContext
     */
    public StackDataModelHandler(Object container, Object rootDataModel, ProcessContext processContext) {
        if (container.getClass().isPrimitive()) {
            throw new IllegalArgumentException("DataModel can not be primitive");
        }
        this.processContext = processContext;
        this.dataModelStack.push(container);
        this.dataModelHelper = new DataModelHelper();
        this.rootDataModel = rootDataModel;
    }

    @Override
    public boolean isLogicalTrueOrFalse(String key) {
        // 实现逻辑非操作
        if (StringUtils.containsAny(key, "=<>&|")) {
            return new CompoundLogicalOperation(key).evaluate(this::parseValueLocalOrGlobal);
        }
        else {
            return new LogicalValue(key).evaluate(this::parseValueLocalOrGlobal);
        }
    }

    @Override
    public boolean isLogicalTrue(Object container, String key) {
        throw new NotImplementedException("Not implemented yet");
    }

    @Override
    public boolean isInEmptyLoop() {
        return getTopDataModel() instanceof LoopModel
                && ((LoopModel) getTopDataModel()).getMatrix().isEmpty();
    }

    public String onKey(String key) {
        Object v = parseValueLocalOrGlobal(key);
        if (v == null) {
            return StringUtils.EMPTY;
        }
        else {
            return v.toString();
        }
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

    /**
     * 获取局部变量或者全局变量
     *
     * @param key
     * @return
     */
    private Object parseValueLocalOrGlobal(String key) {
        Object v;
        Object model = null;
        if (".".equals(key)) {
            model = getTopDataModel();
            v = model;
        }
        else if (key.startsWith(".")) {
            model = getTopDataModel();
            v = dataModelHelper.getValueRecursively(
                    model,
                    StringUtils.substringAfter(key.trim(), ".").trim(),
                    Object.class);
        }
        else {
            model = rootDataModel;
            v = dataModelHelper.getValueRecursively(model, key, Object.class);
        }
        if (v == null) {
            processContext.addMessageToCurrentGroup(String.format("No value in the data model %s by key: %s", model.getClass(), key));
        }
        return v;
    }

    @Override
    public LoopModel onLoop(String loopKey) {
        List<Object> ret = new ArrayList<>();

        // 只能用 Iterable， 因为可能返回各种对象
        Iterable dataMatrix = dataModelHelper.getValueRecursively(getTopDataModel(), loopKey, Iterable.class);
        if (dataMatrix == null || !dataMatrix.iterator().hasNext()) {
            Logger.getInstance().warn(String.format("No collection or key-value object in the data model %s by key: %s", getTopDataModel().getClass(), loopKey));
            processContext.addMessageToCurrentGroup(String.format("No collection or key-value object in the data model %s by key: %s", getTopDataModel().getClass(), loopKey));
            return new LoopModel(ret);
        }

        // 遍历数据模型中的数组（含单值或多值）
        for (Object dataRow : dataMatrix) {
            // 数组成员是 JsonObject，
            if (dataRow instanceof JsonObject) {
                Map<String, Object> m = new HashMap<>();
                for (String k : ((JsonObject) dataRow).keySet()) {
                    // 从数组成员中获值
                    Object v = dataModelHelper.getValueRecursively(dataRow, k, Object.class);
                    if (v != null) {
                        m.put(k, v);
                    }
                }
                ret.add(m);
            }
            else if (dataRow instanceof Map) {
//                for (Object k : ((Map) dataRow).keySet()) {
//                    Object v = dataModelHelper.getValueRecursively(dataRow, (String) k, Object.class);
//                    m.put(k.toString(), v);
//                }
                ret.add(dataRow);
            }
            else if (ObjectUtils.isIterableList(dataRow)) {
                Map<String, Object> m = new HashMap<>();
                int i = 0;
                for (Object v : ((Iterable) dataRow)) {
                    m.put(String.valueOf(i++), v);
                }
                ret.add(m);
            }
            else if (dataRow.getClass().isArray()) {
                Map<String, Object> m = new HashMap<>();
                Object[] arow = (Object[]) dataRow;
                for (int i = 0; i < arow.length; i++) {
                    m.put(String.valueOf(i), arow[i]);
                }
                ret.add(m);
            }
            else if (!ObjectUtils.isPrimitive(dataRow)) {
                Map<String, Object> m = new HashMap<>();
                // Bean对象处理
                Collection<String> allFieldsName = dataModelHelper.getAllFieldsName(dataRow.getClass());
                for (String k : allFieldsName) {
                    Object v = dataModelHelper.getValueRecursively(dataRow, k, Object.class);
                    m.put(k, v);
                }
                ret.add(m);
            }
            else if (dataRow instanceof JsonPrimitive) {
                // JsonPrimitive 需要特殊处理
                ret.add(((JsonPrimitive) dataRow).getAsString());
            }
            else {
                // 其他所有 primitive 的对象，例如 String, Integer 等
                ret.add(dataRow);
            }
        }
        return new LoopModel(ret);
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
