package com.github.swiftech;

import com.github.swiftech.swiftmarker.ProcessContextLegacy;
import org.junit.Test;

/**
 * allen
 */
public class ProcessContextLegacyTest {

    @Test
    public void testit() {
        ProcessContextLegacy ctx = new ProcessContextLegacy();
        ctx.addMessage("Standalone message start");


        // Level 1
        ctx.addGroup("level1A.level2A");
        ctx.addMessageToCurrentGroup("message 1a.2a.1");
        ctx.addGroupMessage("level1A.level2B", "message 1a.2a.2");

        ctx.addGroup("level1B");

        ctx.addMessage("Standalone message end");

        ctx.printAllMessages();
    }
}
