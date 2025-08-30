package com.fagi.bug;

import com.fagi.test.TestIssue;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class PressEnterIssueJavaFXUpstream {
    private TestIssue testIssue;

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
        testIssue = new TestIssue();
        loader.setController(testIssue);
        loader.setRoot(testIssue);
        loader.load();
        stage.setScene(new Scene(testIssue));
        stage.show();
    }

    @Test
    void testEnterKeyInTextArea() throws Exception {
        // Use Platform.runLater to ensure Robot is created on the JavaFX Application Thread
        Platform.runLater(() -> {
            testIssue.requestFocusOnTextArea();
            Robot robot = new Robot();
            robot.keyType(KeyCode.H);
            robot.keyType(KeyCode.E);
            robot.keyType(KeyCode.L);
            robot.keyType(KeyCode.L);
            robot.keyType(KeyCode.O);
            robot.keyPress(KeyCode.ENTER);
            robot.keyRelease(KeyCode.ENTER);
            robot.keyType(KeyCode.COMMA);
            robot.keyType(KeyCode.SPACE);
            robot.keyType(KeyCode.W);
            robot.keyType(KeyCode.O);
            robot.keyType(KeyCode.R);
            robot.keyType(KeyCode.L);
            robot.keyType(KeyCode.D);
        });

        Thread.sleep(100);

        String expectedText = "hello\n, world";

        Assertions.assertEquals(
                expectedText,
                testIssue.getMessage()
        );
    }
}
