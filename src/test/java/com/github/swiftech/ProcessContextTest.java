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
        ProcessContext ctx = new ProcessContext();
        //
        ctx.addGroup("A");
        ctx.addMessageToCurrentGroup("A_1 (addMessageToCurrentGroup)");

        //
        ctx.addGroup("B");
        ctx.addGroupMessage("A", "A_2 (addGroupMessage)");
        ctx.addMessageToCurrentGroup("B_1 (addMessageToCurrentGroup)");

        ctx.addGroup("C");
        ctx.addGroup("C.c");
        ctx.addMessageToCurrentGroup("C_2 (addMessageToCurrentGroup)");
        ctx.addGroupToCurrentGroup("cc");
        ctx.addMessageToCurrentGroup("C_3_(addMessageToCurrentGroup)");

        ctx.addGroup("D");
        ctx.addGroup("D.d").setMandatory(true);

        ctx.addGroup("E");
        ctx.addGroup("E.e");

        ctx.printAllMessages();
    }

}
