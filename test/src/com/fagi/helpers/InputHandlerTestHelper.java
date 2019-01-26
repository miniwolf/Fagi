package com.fagi.helpers;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.InputHandler;
import org.testfx.util.WaitForAsyncUtils;

public class InputHandlerTestHelper {
    public static void addIngoingMessageToInputHandler(InputHandler inputHandler, InGoingMessages msg) {
        inputHandler.addIngoingMessage(msg);
        WaitForAsyncUtils.waitForFxEvents();
    }

    public static void addIngoingMessageToInputHandler(InputHandler inputHandler, InGoingMessages msg, int noExpectedEvents) {
        inputHandler.addIngoingMessage(msg);
        for(int i = 0; i < noExpectedEvents; i++) {
            WaitForAsyncUtils.waitForFxEvents();
        }
    }

}
