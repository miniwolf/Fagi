package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit.ApplicationTest;

public class CreateUserNameTests extends ApplicationTest {
    private Communication communication;
    private MasterLogin masterLogin;

    @BeforeClass
    public static void initialize() {
        System.out.println("Starting CreateUserNameTests");
    }

    @Test
    public void ThereMustBeAValueInTheUsernameField_TheMessageLabelShouldInformOtherwise() {
        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Username cannot be empty", messageLabel.getText());
    }

    @Test
    public void WhenUserExistsOnServerCreatingANewUser_InformNewUser() {
        Mockito.when(communication.getNextResponse()).thenReturn(new UserExists());

        String username = "DinMor";

        TextField usernameTextField = lookup("#username").query();
        clickOn(usernameTextField).write(username);

        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Username is not available", messageLabel.getText());
    }

    @Test
    public void UsernameMustNotContainAnySpecialCharacters_InformNemUser() {
        String username = "Din Mor";

        TextField usernameTextField = lookup("#username").query();
        clickOn(usernameTextField).write(username);

        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Username may not contain special symbols", messageLabel.getText());
    }

    @Test
    public void UsernameIsValidAndAvailable_ShowsCreatePasswordScreen() {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        String username = "username";

        TextField usernameTextField = lookup("#username").query();
        clickOn(usernameTextField).write(username);

        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Assert.assertEquals(LoginState.PASSWORD, masterLogin.getState());
        Assert.assertNotNull(lookup("#passwordRepeat"));
    }

    @Test
    public void ClickingOnBackButton_ResultsInReturningToLoginScreen() {
        String username = "username";

        TextField usernameTextField = lookup("#username").query();
        clickOn(usernameTextField).write(username);

        Node backBtn = lookup("#backBtn").query();
        clickOn(backBtn);

        Assert.assertNotNull(lookup("#UniqueLoginScreen").query());
    }

    @Test
    public void WritingUsernameClickingOnBackButtonAndGoingBackToCreateUsername_ResultsInUsernameFieldCleared() {
        String username = "username";

        TextField usernameTextField = lookup("#username").query();
        clickOn(usernameTextField).write(username);

        Node backBtn = lookup("#backBtn").query();
        clickOn(backBtn);

        Node btn = lookup("#newAccount").query();
        clickOn(btn);

        usernameTextField = lookup("#username").query();

        Assert.assertNull(usernameTextField.getText());
    }

    @Test
    public void GoingBackFromCreatePassword_ShouldKeepUsername() {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        String username = "username";

        TextField usernameTextField = lookup("#username").query();
        clickOn(usernameTextField).write(username);

        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Node backBtn = lookup("#backBtn").query();
        clickOn(backBtn);

        usernameTextField = lookup("#username").query();

        System.out.println(usernameTextField.getText());

        Assert.assertNotNull(usernameTextField.getText());
    }

    @Override
    public void start(Stage stage) {
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Draggable draggable = new Draggable(stage);

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
