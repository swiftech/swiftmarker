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
        ctx.addMessageToRootGroup("Standalone message start");

        ctx.addMessageToRootGroup("Message in current group");
        // Level 1
        ctx.addGroup("level1A.level2A");
        ctx.addMessageToCurrentGroup("message 1a.2a.1");
        ctx.addGroupMessage("level1A.level2B", "message 1a.2b.2");

        ctx.addGroup("level1B");

        ctx.addMessageToRootGroup("Standalone message end");

        ctx.printAllMessages();
    }


    @Test
    public void testContext() {
        ProcessContext processContext = new ProcessContext();
        //
        processContext.addGroup("A");
        processContext.addMessageToCurrentGroup("A_1 (addMessageToCurrentGroup)");

        //
        processContext.addGroup("B");
        processContext.addGroupMessage("A", "A_2 (addGroupMessage)");
        processContext.addMessageToCurrentGroup("B_1 (addMessageToCurrentGroup)");

        processContext.addGroup("C");
        processContext.addGroup("C.c");
        processContext.addMessageToCurrentGroup("C_1 (addMessageToCurrentGroup)");

        processContext.printAllMessages();
    }

}
