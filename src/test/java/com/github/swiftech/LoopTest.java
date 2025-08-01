package com.github.swiftech;

import com.github.swiftech.datamodel.Option;
import com.github.swiftech.swiftmarker.Logger;
import com.github.swiftech.swiftmarker.ProcessContext;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 测试循环表达式
 *
 * @author swiftech 2018-11-25
 **/
public class LoopTest extends BaseResourceTest {

    @BeforeEach
    public void setup() {
        log.setLevel(Logger.LEVEL_DEBUG);
        engine.setConfig(config);
    }

    /**
     * 测试空循环
     */
    @Test
    public void testEmptyLoop() {
        String rendered = super.runFromResourceAndAssert("loop/empty", "loop/basic");
        log.data(rendered);
    }

    /**
     * 测试基本的循环
     */
    @Test
    public void testBasicLoop() {
        String rendered = super.runFromResourceAndAssert("loop/basic");
        log.data(rendered);
    }

    /**
     * 测试重复的基本的循环
     */
    @Test
    public void testBasicRepeatLoop() {
        String rendered = super.runFromResourceAndAssert("loop/basic_repeat", "loop/basic");
        log.data(rendered);
    }

    /**
     * 测试基本的循环（纯嵌套语句）
     */
    @Test
    public void testBasicBareLoop() {
        String rendered = super.runFromResourceAndAssert("loop/basic_bare", "loop/basic");
        log.data(rendered);
    }

    /**
     * 测试基本的单行循环
     */
    @Test
    public void testBasicLoopInLine() {
        String rendered = super.runFromResourceAndAssert("loop/basic_in_line", "loop/basic");
        log.data(rendered);
    }

    @Test
    public void testMultiLoopInLine() {
        String rendered = super.runFromResourceAndAssert("loop/multi_in_line", "loop/basic");
        log.data(rendered);
    }

    /**
     * 测试循环表达式中变量的作用域
     */
    @Test
    public void testBasicScope() {
        String rendered = super.runFromResourceAndAssert("loop/basic_scope", "loop/basic");
        log.data(rendered);
    }

    /**
     * 测试复杂的多行循环
     */
    @Test
    public void testComplexTemplate() {
        engine.setTemplate("${question.title}\n" +
                "$[question.options]\n" +
                "${index} :\n" +
                "  ${option}\n" +
                "$[]\n" +
                "$[question.options]${index} = ${option}$[]\n" +
                "\n" +
                "${question.end}\n" +
                "\n" +
                "the end");

        String json = "{\n" +
                "  \"question\": {\n" +
                "      \"title\": \"What's your favorite color?\",\n" +
                "       \"end\": \"hahaha\",\n" +
                "      \"options\": [\n" +
                "          {\"index\": \"A\", \"option\": \"Red\"},\n" +
                "          {\"index\": \"B\", \"option\": \"Green\"},\n" +
                "          {\"index\": \"C\", \"option\": \"Blue\"}\n" +
                "      ]            \n" +
                "  }\n" +
                "}";
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        String s = engine.process(jsonObject, new ProcessContext());
        log.info(s);
    }

    /**
     * 测试纯对象反射的数据模型
     */
    @Test
    public void testLoopByObject() {
        engine.setTemplate(super.loadTemplate("loop/basic"));

        final JsonObject rowB = new JsonObject();
        rowB.add("index", new JsonPrimitive("B"));
        rowB.add("option", new JsonPrimitive("Green"));

        Object o = new Object() {
            final Object question = new Object() {
                final String title = "What's your favorite color?";
                final Object options = new ArrayList<>() {
                    {
                        add(new HashMap<String, String>() {
                            {
                                put("index", "A");
                                put("option", "Red");
                            }
                        });
                        add(rowB);
                        add(new Option("C", "Blue", "多余的属性"));
                    }
                };
            };
        };

        String s = engine.process(o, new ProcessContext());
        log.data(s);
    }

    /**
     * 测试数组的数据模型
     */
    @Test
    public void testLoopArray() {
        engine.setTemplate("""
                ${question.title}
                $[question.options]${0} : ${1}$[]
                the end""");

        final JsonArray jac = new JsonArray();
        jac.add("C");
        jac.add("Blue");

        Object o = new Object() {
            final Object question = new Object() {
                final String title = "What's your favorite color?";
                final Object[] options = new Object[]{
                        new Object[]{"A", "Red"},
                        Arrays.asList("B", "Green"),
                        new Object[]{"C", "Blue"},
                };
            };
        };

        String s = engine.process(o, new ProcessContext());
        log.data(s);
    }

    /**
     * 测试循环元素为集合、数组的情况
     */
    @Test
    public void testWithIndex() {
        String rendered = super.runFromResourceAndAssert("loop/with_index");
        log.data(rendered);
    }

    /**
     *
     */
    @Test
    public void testNestedLoop() {
        String rendered = super.runFromResourceAndAssert("loop/nested");
        log.data(rendered);
    }

    @Test
    public void testNestedLoopSimple() {
        String rendered = super.runFromResourceAndAssert("loop/nested", "loop/nested_simple", "loop/nested_simple");
        log.data(rendered);
    }

    /**
     * 测试循环嵌套逻辑
     */
    @Test
    public void testLoopNestLogic() {
        String rendered = super.runFromResourceAndAssert("loop/nested_logic", "loop/nested");
        log.data(rendered);
    }


    @Test
    public void testNestedLogicNoData() {
        String rendered = super.runFromResourceAndAssert("loop/nested_logic", "no_data", "loop/nested_no_data");
        log.data(rendered);
    }


    /**
     * 测试循环嵌套逻辑（全局变量）
     */
    @Test
    public void testLoopNestLogicGlobal() {
        String rendered = super.runFromResourceAndAssert("loop/nested_logic_global", "loop/nested");
        log.data(rendered);
    }

    /**
     * 测试循环嵌套多个逻辑
     */
    @Test
    public void testLoopNestLogic2() {
        String rendered = super.runFromResourceAndAssert("loop/nested_logic2", "loop/nested");
        log.data(rendered);
    }

    /**
     * 测试循环嵌套2层逻辑
     */
    @Test
    public void testLoopNestLogic2l() {
        String rendered = super.runFromResourceAndAssert("loop/nested_logic2l", "loop/nested");
        log.data(rendered);
    }

    @Test
    public void testLoopWithLogicLame() {
        Assertions.assertThrows(RuntimeException.class
                , () -> runFromResourceAndAssert("logic/with_logic_lame", "logic/with_loop", "logic/with_logic_lame"));
    }

}
