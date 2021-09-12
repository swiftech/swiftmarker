package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author swiftech 2018-12-25
 **/
public class LoopWrongTest extends BaseResourceTest {

    @Test
    public void testWrongLoop() {
        engine.setTemplate("${question.title}\n" +
                "$[question.options]${index}: ${option}$[]" +
                "\n" +
                "the end");

        String json = "{\n" +
                "  \"question\": {\n" +
                "      \"title\": [\"What's your favorite color?\"],\n" +
                "      \"options\": \"haha\",\n" +
                "      \"opts\": [\n" +
                "          {\"index\": \"A\", \"option\": \"Red\"},\n" +
                "          {\"index\": \"B\", \"option\": \"Green\"},\n" +
                "          {\"index\": \"C\", \"option\": \"Blue\"}\n" +
                "      ]\n" +
                "  }\n" +
                "}";
        System.out.println(json);
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        Assertions.assertThrows(Exception.class, () -> {
            String s = engine.process(jsonObject, new ProcessContext());
            log.data(s);
        });
    }

    @Test
    public void testWrongArray() {
        engine.setTemplate("What's your favorite color?\n" +
                "$[question.options]${index}: ${option}$[]");

        String json = loadJsonData("loop/basic_less");
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        String s = engine.process(jsonObject, new ProcessContext());
        log.info(s);
    }
}
