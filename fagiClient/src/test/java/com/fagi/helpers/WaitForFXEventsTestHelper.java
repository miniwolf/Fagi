package com.fagi.helpers;

import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.InputHandler;
import javafx.scene.Node;
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxRobot;
import org.testfx.util.WaitForAsyncUtils;

public class WaitForFXEventsTestHelper {
    public static void addIngoingMessageToInputHandler(
            InputHandler inputHandler,
            InGoingMessages msg) {
        inputHandler.addIngoingMessage(msg);
        WaitForAsyncUtils.waitForFxEvents();
    }

    public static void addIngoingMessageToInputHandler(
            InputHandler inputHandler,
            InGoingMessages msg,
            int noExpectedEvents) {
        inputHandler.addIngoingMessage(msg);
        for (int i = 0; i < noExpectedEvents; i++) {
            WaitForAsyncUtils.waitForFxEvents();
        }
    }

    public static void clickOn(
            FxRobot robot,
            String query) {
        Assertions.assertTrue(robot
                                      .lookup(query)
                                      .tryQuery()
                                      .isPresent(), "Could not find: " + query);
        robot.clickOn(query);
        WaitForAsyncUtils.waitForFxEvents();
    }

    public static void clickOn(
            FxRobot robot,
            Node node) {
        robot.clickOn(node);
        WaitForAsyncUtils.waitForFxEvents();
    }

    public static void clickOnAndWrite(
            FxRobot robot,
            String query,
            String message) {
        Assertions.assertTrue(robot
                                      .lookup(query)
                                      .tryQuery()
                                      .isPresent(), "Could not find: " + query);
        robot
                .clickOn(query)
                .write(message);
        WaitForAsyncUtils.waitForFxEvents();
    }

    public static void clickOnAndWrite(
            FxRobot robot,
            Node node,
            String message) {
        robot
                .clickOn(node)
                .write(message);
        WaitForAsyncUtils.waitForFxEvents();
    }
}
