package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Allen 2019-08-23
 **/
public class PracticeTest extends BaseResourceTest {
    @BeforeEach
    public void setup() {
//        log.setLevel(Logger.LEVEL_DEBUG);
        engine.setConfig(config);
    }

    @Test
    public void testVerySimple(){
        String temp = "/template/backend/{locale}/src/main/java/model/entity/Base${project_name}Entity.java.tpl";
        super.engine.setTemplate(temp);
        Object model = new Object(){
            String project_name = "FooBar";
        };
        String result = super.engine.process(model, new ProcessContext());
        System.out.println(result);
    }

    /**
     * 测试空循环
     */
    @Test
    public void testEntityField() {
        String rendered = super.runFromResourceAndAssert("practice/entity_field", "practice/entity_field");
        log.data(rendered);
    }


    @Test
    public void testLoopWithBrace() {
        String rendered = super.runFromResourceAndAssert("practice/loop_with_brace");
        log.data(rendered);
    }
}
