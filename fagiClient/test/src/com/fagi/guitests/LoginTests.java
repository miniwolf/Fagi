package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.*;
import com.fagi.util.DefaultWiringModule;
import com.fagi.util.DependencyInjectionSystem;
import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

import java.io.IOException;

public class LoginTests extends GuiTest {
    MasterLogin spy;
    Communication communication;

    @Test
    public void LoginWithWrongPassword_WillPostCorrectErrorMessage() {
        String username = "username";
        Node usernameField = lookup("#username").query();
        clickOn(usernameField).write(username);

        String password = "password";
        Node passwordField = lookup("#password").query();
        clickOn(passwordField).write(password);

        Node loginBtn = lookup("#loginBtn").query();
        clickOn(loginBtn);
    }

    @Test
    public void WhenFocusedOnFieldAndTyping_TextIsContainedInFields() {
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
        spy.setMessageLabel("Connection refused");

        Node messageLabel = lookup("#messageLabel").query();
        Assert.assertEquals("Connection refused", ((Label)messageLabel).getText());
    }

    @Test
    public void WhenClickingOnCreateNewUser_ShowCreateUsernameScreen() {
        Node btn = lookup("#newAccount").query();
        clickOn(btn);

        Assert.assertEquals(LoginState.USERNAME, spy.getState());
        Assert.assertNotNull(lookup("#UniqueCreateUsernameView").query());
    }

    @Override
    protected Parent getRootNode() {
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Stage stage = (Stage) targetWindow();
        stage.setScene(new Scene(new AnchorPane()));
        Draggable draggable = new Draggable(stage);

        communication = Mockito.mock(Communication.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        DependencyInjectionSystem.setModule(Modules.override(
                new DefaultWiringModule()).with(new AbstractModule() {
            @Override
            protected void configure() {
                this.bind(Communication.class).toInstance(communication);
            }
        }));

        MasterLogin masterLogin = new MasterLogin(fagiApp, stage, draggable);
        spy = Mockito.spy(masterLogin);

        Mockito.doNothing().when(spy).updateRoot();
        spy.showMasterLoginScreen();
        Mockito.doCallRealMethod().when(spy).updateRoot();

        return spy.getController().getParentNode();
    }
}
