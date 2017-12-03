package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.util.DefaultWiringModule;
import com.fagi.util.DependencyInjectionSystem;
import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

import java.awt.*;

public class CreatePasswordTests extends GuiTest {
    private Communication communication;
    private MasterLogin spy;

    @Test
    public void RepeatPasswordMustMatchPassword_ErrorInformUser() {
        String password = "thisisapassword";
        String repeat = "thisisadiffrentpassword";

        PasswordField pwfield = lookup("#password").query();
        clickOn(pwfield).write(password);
        PasswordField pwRepeatfield = lookup("#passwordRepeat").query();
        clickOn(pwRepeatfield).write(repeat);

        Node btn = lookup("#loginBtn").query();
        clickOn(btn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Passwords does not match", messageLabel.getText());
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
        masterLogin.setState(LoginState.PASSWORD);
        spy = Mockito.spy(masterLogin);

        Mockito.doNothing().when(spy).updateRoot();
        spy.showMasterLoginScreen();
        Mockito.doCallRealMethod().when(spy).updateRoot();
        return spy.getController().getParentNode();
    }
}
