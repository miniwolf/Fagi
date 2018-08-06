package com.fagi.guitests;

import com.fagi.config.ServerConfig;
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
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit.ApplicationTest;

import java.io.IOException;

public class LoginTests extends ApplicationTest {
    private MasterLogin spy;
    private Communication communication;

    @BeforeClass
    public static void onlyOnce() {
        System.out.println("onlyOnce");
        ServerConfig config = new ServerConfig("test", "127.0.0.1", 1337, null);
        try {
            config.saveToPath("config/serverinfo.config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void WhenFocusedOnFieldAndTyping_TextIsContainedInFields() {
        System.out.println("WhenFocusedOnFieldAndTyping_TextIsContainedInFields");
        String testText = "ThisTextShould Exist";
        Node usernameField = lookup("#username").query();
        clickOn(usernameField).write(testText);

        Assert.assertEquals(((TextField) usernameField).getText(), testText);

        Node passwordField = lookup("#password").query();
        clickOn(passwordField).write(testText);
        Assert.assertEquals(((PasswordField) passwordField).getText(), testText);
    }

    @Test
    public void WhenGivingWrongPassword_ErrorMessageShouldShowThis() {
        System.out.println("WhenGivingWrongPassword_ErrorMessageShouldShowThis");
        Mockito.when(communication.getNextResponse()).thenReturn(new PasswordError());

        String username = "dinMor";
        String password = "password";
        String errorMsg = "Wrong password";

        Node usernameField = lookup("#username").query();
        clickOn(usernameField).write(username);

        Node passwordField = lookup("#password").query();
        clickOn(passwordField).write(password);

        Label messageLabel = lookup("#messageLabel").query();

        Node signinBtn = lookup("#loginBtn").query();
        clickOn(signinBtn);

        Assert.assertEquals(errorMsg, messageLabel.getText());
    }

    @Test
    public void WhenGivingWrongUsername_ErrorMessageShouldShowThis() {
        System.out.println("WhenGivingWrongUsername_ErrorMessageShouldShowThis");
        Mockito.when(communication.getNextResponse()).thenReturn(new NoSuchUser());

        String username = "dinMor";
        String password = "password";
        String errorMsg = "User doesn't exist";

        Node usernameField = lookup("#username").query();
        clickOn(usernameField).write(username);

        Node passwordField = lookup("#password").query();
        clickOn(passwordField).write(password);

        Node messageLabel = lookup("#messageLabel").query();

        Node signinBtn = lookup("#loginBtn").query();
        clickOn(signinBtn);

        Assert.assertEquals(errorMsg, ((Label)messageLabel).getText());
    }

    @Test
    public void WhenUserIsAlreadyOnline_ErrorMessageShouldShowThis() {
        System.out.println("WhenUserIsAlreadyOnline_ErrorMessageShouldShowThis");
        Mockito.when(communication.getNextResponse()).thenReturn(new UserOnline());

        String username = "dinMor";
        String password = "password";
        String errorMsg = "You are already online";

        Node usernameField = lookup("#username").query();
        clickOn(usernameField).write(username);

        Node passwordField = lookup("#password").query();
        clickOn(passwordField).write(password);

        Node messageLabel = lookup("#messageLabel").query();

        Node signinBtn = lookup("#loginBtn").query();
        clickOn(signinBtn);

        Assert.assertEquals(errorMsg, ((Label)messageLabel).getText());
    }

    @Test
    public void WhenCallingSetMessageLabel_NewMessageShouldAppear() {
        System.out.println("WhenCallingSetMessageLabel_NewMessageShouldAppear");
        spy.setMessageLabel("Connection refused");

        Label messageLabel = lookup("#messageLabel").query();
        Assert.assertEquals("Connection refused", messageLabel.getText());
    }

    @Test
    public void WhenClickingOnCreateNewUser_ShowCreateUsernameScreen() {
        System.out.println("WhenClickingOnCreateNewUser_ShowCreateUsernameScreen");
        System.out.println("Dimmer6");
        Node btn = lookup("#newAccount").query();
        System.out.println("Dimmer4");
        clickOn(btn);
        System.out.println("Dimmer5");

        System.out.println("Dimmer");
        Assert.assertEquals(LoginState.USERNAME, spy.getState());
        System.out.println("Dimmer2");
        Assert.assertNotNull(lookup("#UniqueCreateUsernameView").query());
        System.out.println("Dimmer3");
    }

    @Override
    public void start(Stage stage) {
        System.out.println("start");
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Draggable draggable = new Draggable(stage);

        communication = Mockito.mock(Communication.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        MasterLogin masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
        spy = Mockito.spy(masterLogin);

        Mockito.doNothing().when(spy).updateRoot();
        spy.showMasterLoginScreen();
        Mockito.doCallRealMethod().when(spy).updateRoot();

        stage.setScene(new Scene(spy.getController().getParentNode()));
        stage.show();
    }
}
