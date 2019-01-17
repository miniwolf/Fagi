package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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
public class CreateUserNameTests {
    private Communication communication;
    private MasterLogin masterLogin;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting CreateUserNameTests");
    }

    @Test
    public void ThereMustBeAValueInTheUsernameField_TheMessageLabelShouldInformOtherwise(FxRobot robot) {
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Username cannot be empty", messageLabel.getText());
    }

    @Test
    public void WhenUserExistsOnServerCreatingANewUser_InformNewUser(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new UserExists());

        robot.clickOn("#username").write("DinMor");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Username is not available", messageLabel.getText());
    }

    @Test
    public void UsernameMustNotContainAnySpecialCharacters_InformNemUser(FxRobot robot) {
        robot.clickOn("#username").write("Din Mor");
        robot.clickOn("#loginBtn");

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Username may not contain special symbols", messageLabel.getText());
    }

    @Test
    public void UsernameIsValidAndAvailable_ShowsCreatePasswordScreen(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        robot.clickOn("#username").write("username");
        robot.clickOn("#loginBtn");

        Assertions.assertEquals(LoginState.PASSWORD, masterLogin.getState());
        Assertions.assertNotNull(robot.lookup("#passwordRepeat"));
    }

    @Test
    public void ClickingOnBackButton_ResultsInReturningToLoginScreen(FxRobot robot) {
        robot.clickOn("#username").write("username");
        robot.clickOn("#backBtn");

        Assertions.assertNotNull(robot.lookup("#UniqueLoginScreen").query());
    }

    @Test
    public void WritingUsernameClickingOnBackButtonAndGoingBackToCreateUsername_ResultsInUsernameFieldCleared(FxRobot robot) {
        robot.clickOn("#username").write("username");
        robot.clickOn("#backBtn");
        robot.clickOn("#newAccount");

        TextField usernameTextField = robot.lookup("#username").query();

        Assertions.assertNull(usernameTextField.getText());
    }

    @Test
    public void GoingBackFromCreatePassword_ShouldKeepUsername(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        robot.clickOn("#username").write("username");
        robot.clickOn("#loginBtn");
        robot.clickOn("#backBtn");

        TextField usernameTextField = robot.lookup("#username").query();

        Assertions.assertNotNull(usernameTextField.getText());
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
        masterLogin.setState(LoginState.USERNAME);
        masterLogin.showMasterLoginScreen();

        stage.getScene().setRoot(masterLogin.getController().getParentNode());
        stage.show();
    }
}
