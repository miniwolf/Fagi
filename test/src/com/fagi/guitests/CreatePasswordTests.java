package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
        Node nextBtn = robot.lookup("#loginBtn").query();
        robot.clickOn(nextBtn);

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Password field must not be empty", messageLabel.getText());
    }

    @Test
    public void PasswordRepeatFieldMustHaveAValue_MessageShouldIndicateOtherwise(FxRobot robot) {
        String password = "thisisapassword";

        PasswordField pwfield = robot.lookup("#password").query();
        robot.clickOn(pwfield).write(password);

        Node nextBtn = robot.lookup("#loginBtn").query();
        robot.clickOn(nextBtn);

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Repeat password field must not be empty", messageLabel.getText());
    }

    @Test
    public void RepeatPasswordMustMatchPassword_ErrorInformUser(FxRobot robot) {
        String password = "thisisapassword";
        String repeat = "thisisadiffrentpassword";

        PasswordField pwfield = robot.lookup("#password").query();
        robot.clickOn(pwfield).write(password);
        PasswordField pwRepeatfield = robot.lookup("#passwordRepeat").query();
        robot.clickOn(pwRepeatfield).write(repeat);

        Node btn = robot.lookup("#loginBtn").query();
        robot.clickOn(btn);

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Passwords does not match", messageLabel.getText());
    }

    @Test
    public void SuccessfulPasswordCreation_ShouldShowInviteCodeScreen(FxRobot robot) {
        String password = "thisisapassword";

        PasswordField pwfield = robot.lookup("#password").query();
        robot.clickOn(pwfield).write(password);
        PasswordField pwRepeatfield = robot.lookup("#passwordRepeat").query();
        robot.clickOn(pwRepeatfield).write(password);

        Node btn = robot.lookup("#loginBtn").query();
        robot.clickOn(btn);

        Assertions.assertEquals(LoginState.INVITE_CODE, masterLogin.getState());
        Assertions.assertNotNull(robot.lookup("#UniqueInviteCode"));
    }

    @Start
    public void start(Stage stage) {
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Draggable draggable = new Draggable(stage);

        Communication communication = Mockito.mock(Communication.class);

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
