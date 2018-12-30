package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.UserOnline;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class LoginTests {
    private MasterLogin masterLogin;
    private Communication communication;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting login tests");
    }

    @Test
    public void WhenFocusedOnFieldAndTyping_TextIsContainedInFields(FxRobot robot) {
        var testText = "ThisTextShould Exist";
        TextField usernameField = robot.lookup("#username").query();
        robot.clickOn(usernameField).write(testText);

        Assertions.assertEquals(testText, usernameField.getText());

        PasswordField passwordField = robot.lookup("#password").query();
        robot.clickOn(passwordField).write(testText);

        Assertions.assertEquals(testText, passwordField.getText());
    }

    @Test
    public void WhenGivingWrongPassword_ErrorMessageShouldShowThis(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new PasswordError());

        robot.clickOn("#username").write("dinMor");
        robot.clickOn("#password").write("password");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();
        Assertions.assertEquals("Wrong password", messageLabel.getText());
    }

    @Test
    public void WhenGivingWrongUsername_ErrorMessageShouldShowThis(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new NoSuchUser());

        robot.clickOn("#username").write("dinMor");
        robot.clickOn("#password").write("password");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();
        Assertions.assertEquals("User doesn't exist", messageLabel.getText());
    }

    @Test
    public void WhenUserIsAlreadyOnline_ErrorMessageShouldShowThis(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new UserOnline());

        robot.clickOn("#username").write("dinMor");
        robot.clickOn("#password").write("password");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();
        Assertions.assertEquals("You are already online", messageLabel.getText());
    }

    @Test
    public void WhenCallingSetMessageLabel_NewMessageShouldAppear(FxRobot robot) {
        masterLogin.setMessageLabel("Connection refused");

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Label messageLabel = robot.lookup("#messageLabel").query();
        Assertions.assertEquals("Connection refused", messageLabel.getText());
    }

    @Test
    public void WhenClickingOnCreateNewUser_ShowCreateUsernameScreen(FxRobot robot) {
        robot.clickOn("#newAccount");

        Assertions.assertEquals(LoginState.USERNAME, masterLogin.getState());
        Assertions.assertTrue(
                robot.lookup("#UniqueCreateUsernameView").tryQuery().isPresent(),
                "Should switch to Create Username View.");
    }

    @Start
    public void start(Stage stage) {
        var fagiApp = Mockito.mock(FagiApp.class);
        var draggable = new Draggable(stage);

        communication = Mockito.mock(Communication.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
        masterLogin.showMasterLoginScreen();

        stage.getScene().setRoot(masterLogin.getController().getParentNode());
        stage.show();
    }
}
