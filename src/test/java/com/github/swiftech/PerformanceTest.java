package com.github.swiftech;

import com.github.swiftech.swiftmarker.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Test;

/**
 * @author swiftech
 */
public class PerformanceTest extends BaseResourceTest {

    /**
     * 测试性能
     */
    @Test
    public void testPerformance() {
        int repeat = 1;
        Logger.getInstance().setLevel(Logger.LEVEL_WARN);
        engine.setTemplate(this.loadTemplate("practice/entity_field"));
        String json = this.loadJsonData("practice/entity_field");
        log.data(json);
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        long start = System.currentTimeMillis();
        for (int i = 0; i < repeat; i++) {
            String result = this.processWithContext(jsonObject);
        }
        long end = System.currentTimeMillis();
        System.out.printf("Rendered %d times with time %.2fs%n", repeat, (end - start) / 1000.0);
    }
}
