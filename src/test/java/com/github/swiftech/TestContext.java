package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 * @author Allen 2019-03-31
 **/
public class TestContext extends BaseResourceTest {

    @Test
    public void testWithProcessing() {
        ProcessContext processContext = new ProcessContext();
        {
            //
            processContext.addGroup("simple/basic");
            engine.setTemplate(loadTemplate("simple/basic"));
            JsonObject data1 = new JsonObject();
            JsonObject sub = new JsonObject();
            sub.addProperty("word1", "anger");
            sub.addProperty("word2", "hate");
            data1.add("yoda", sub);
            processContext.addMessageToCurrentGroup(StringUtils.abbreviate(data1.toString(), 30));
            engine.process(data1, processContext);

            //
            processContext.addGroup("logic/basic");
            processContext.addGroupMessage("simple.basic", "串了？");
            engine.setTemplate(loadTemplate("logic/basic"));
            JsonObject data2 = new Gson().fromJson(this.loadJsonData("logic/basic"), JsonObject.class);
            processContext.addMessageToCurrentGroup(StringUtils.abbreviate(data2.toString(), 30));
            engine.process(data2, processContext);
        }
        processContext.printAllMessages();
    }
}
