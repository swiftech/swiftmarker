package com.github.swiftech;

import com.github.swiftech.swiftmarker.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Allen 2018-12-16
 **/
public class BaseResourceTest {

    protected Config config = new Config();
    protected Logger log = Logger.getInstance();
    protected TemplateEngine engine = new TemplateEngine();
    protected ProcessContext processContext;

    /**
     * 所有名称都一致
     *
     * @param resName
     * @return
     */
    public String runFromResourceAndAssert(String resName) {
        return runFromResourceAndAssert(resName, resName);
    }

    /**
     * assert 的名称和 模板名称一致
     *
     * @param templateResName
     * @param dataResName
     * @return
     */
    public String runFromResourceAndAssert(String templateResName, String dataResName) {
        return runFromResourceAndAssert(templateResName, dataResName, templateResName);
    }

    public String runFromResourceAndAssert(String templateResName, String dataResName, String assertResName) {
        String actual = runFromResource(templateResName, dataResName);
        assertRender(assertResName, actual);
        return actual;
    }

    public void assertRender(String resName, String actual) {
        Assert.assertEquals(String.format("验证模板%s错误", resName), loadExpect(resName), actual);
    }

    /**
     * 从资源文件加载并执行测试
     *
     * @param resName
     * @return
     */
    public String runFromResource(String resName) {
        return this.runFromResource(resName, resName);
    }

    /**
     * 从资源文件加载并执行测试
     *
     * @param templateResName
     * @param dataResName
     * @return
     */
    public String runFromResource(String templateResName, String dataResName) {
        engine.setTemplate(this.loadTemplate(templateResName));
        String json = this.loadJsonData(dataResName);
        log.data(json);
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
//        ProcessContext processContext = new ProcessContext();
//        String process = engine.process(jsonObject, processContext);
//        processContext.printAllMessages();
        return this.processWithContext(jsonObject);
    }

    public String processWithContext(Object data) {
        processContext = new ProcessContext();
        String process = engine.process(data, processContext);
        processContext.printAllMessages();
        return process;
    }

    protected String loadTemplate(String name) {
        InputStream resourceAsStream =
                this.getClass().getResourceAsStream("/template/" + name);
        String s = null;
        try {
            s = IOUtils.toString(resourceAsStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    protected String loadJsonData(String name) {
        InputStream resourceAsStream =
                this.getClass().getResourceAsStream("/data/" + name + ".json");
        String s = null;
        try {
            s = IOUtils.toString(resourceAsStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    protected String loadExpect(String name) {
        String uri = String.format("/assert/%s", name);
        InputStream resourceAsStream =
                this.getClass().getResourceAsStream(uri);
        if (resourceAsStream == null) {
            log.error("Assert resource not exist:" + uri);
        }
        String s = null;
        try {
            s = IOUtils.toString(resourceAsStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
