package com.fagi.bug;

import com.fagi.test.TestIssue;
import com.fagi.utility.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;

@ExtendWith(ApplicationExtension.class)
public class PressEnterIssue {
    @Test
    void PressingEnterInConversationField_WillFillLabel(FxRobot robot) throws IOException {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        robot
                .clickOn("#conversationTextarea")
                .write("Hello")
                .push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Start
    public void start(Stage stage) throws IOException {
        stage.setScene(new Scene(new AnchorPane()));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
        var parent = new TestIssue();
        loader.setController(parent);
        loader.setRoot(parent);
        try {
            loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Logger.logStackTrace(ioe);
        }
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
