package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
public class CreatePasswordTests {
    private MasterLogin masterLogin;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting CreatePasswordTests");
    }

    @Test
    public void PasswordFieldMustHaveAValue_MessageShouldIndicateOtherwise(FxRobot robot) {
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Password field must not be empty", messageLabel.getText());
    }

    @Test
    public void PasswordRepeatFieldMustHaveAValue_MessageShouldIndicateOtherwise(FxRobot robot) {
        robot.clickOn("#password").write("thisisapassword");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Repeat password field must not be empty", messageLabel.getText());
    }

    @Test
    public void RepeatPasswordMustMatchPassword_ErrorInformUser(FxRobot robot) {
        robot.clickOn("#password").write("thisisapassword");
        robot.clickOn("#passwordRepeat").write("thisisadiffrentpassword");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Passwords does not match", messageLabel.getText());
    }

    @Test
    public void SuccessfulPasswordCreation_ShouldShowInviteCodeScreen(FxRobot robot) {
        var password = "thisisapassword";

        robot.clickOn("#password").write(password);
        robot.clickOn("#passwordRepeat").write(password);
        robot.clickOn("#loginBtn");

        Assertions.assertEquals(LoginState.INVITE_CODE, masterLogin.getState());
        Assertions.assertNotNull(robot.lookup("#UniqueInviteCode"));
    }

    @Start
    public void start(Stage stage) {
        var fagiApp = Mockito.mock(FagiApp.class);
        var draggable = new Draggable(stage);

        var communication = Mockito.mock(Communication.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
        masterLogin.setState(LoginState.PASSWORD);
        masterLogin.showMasterLoginScreen();

        stage.getScene().setRoot(masterLogin.getController().getParentNode());
        stage.show();
    }
}
