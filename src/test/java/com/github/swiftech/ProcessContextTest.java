package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContext;
import org.junit.Test;

/**
 * allen
 */
public class ProcessContextTest {

    /**
     * 测试处理过程中的记录机制
     */
    @Test
    public void testProcessMessages() {
        ProcessContext ctx = new ProcessContext();
        ctx.addMessageToCurrentGroup("Standalone message start");


        // Level 1
        ctx.addGroup("level1A.level2A");
        ctx.addMessageToCurrentGroup("message 1a.2a.1");
        ctx.addGroupMessage("level1A.level2B", "message 1a.2a.2");

        ctx.addGroup("level1B");

        ctx.addMessageToCurrentGroup("Standalone message end");

        ctx.printAllMessages();
    }


    @Test
    public void testContext() {
        ProcessContext processContext = new ProcessContext();
        //
        processContext.addGroup("A");
        processContext.addMessageToCurrentGroup("A - addMessageToCurrentGroup");

        //
        processContext.addGroup("B");
        processContext.addGroupMessage("A", "A - addGroupMessage");
        processContext.addMessageToCurrentGroup("B - addMessageToCurrentGroup");

        processContext.printAllMessages();
    }

}
