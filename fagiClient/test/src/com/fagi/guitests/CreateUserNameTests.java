package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import com.fagi.util.DefaultWiringModule;
import com.fagi.util.DependencyInjectionSystem;
import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

public class CreateUserNameTests extends GuiTest {
    private Communication communication;
    private MasterLogin spy;

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

        Assert.assertEquals(LoginState.PASSWORD, spy.getState());
        Assert.assertNotNull(lookup("#passwordRepeat"));
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
        masterLogin.setState(LoginState.USERNAME);
        spy = Mockito.spy(masterLogin);

        Mockito.doNothing().when(spy).updateRoot();
        spy.showMasterLoginScreen();
        Mockito.doCallRealMethod().when(spy).updateRoot();
        return spy.getController().getParentNode();
    }
}
