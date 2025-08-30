package com.fagi.main;

import com.fagi.test.TestIssue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/test.fxml"));
        TestIssue root = new TestIssue();
        loader.setController(root);
        loader.setRoot(root);
        loader.load();

        stage.setScene(new Scene(root));
        stage.show();

        // Use JavaFX Robot to simulate pressing Enter
        Robot robot = new Robot();
        Thread.sleep(2000); // Wait for the window to appear
        robot.mouseMove(
                100,
                300
        ); // Move to the TextArea
        robot.mouseClick(javafx.scene.input.MouseButton.PRIMARY);
        robot.keyType(KeyCode.H); // Type something
        robot.keyType(KeyCode.E);
        robot.keyType(KeyCode.L);
        robot.keyType(KeyCode.L);
        robot.keyType(KeyCode.O);
        robot.keyType(KeyCode.ENTER); // Press Enter
    }

    public static void main(String[] args) {
        launch(args);
    }
}
