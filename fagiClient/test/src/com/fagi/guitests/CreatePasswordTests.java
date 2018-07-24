package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
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

public class CreatePasswordTests extends GuiTest {
    private Communication communication;
    private MasterLogin spy;

    @Test
    public void PasswordFieldMustHaveAValue_MessageShouldIndicateOtherwise() {
        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Password field must not be empty", messageLabel.getText());
    }

    @Test
    public void PasswordRepeatFieldMustHaveAValue_MessageShouldIndicateOtherwise() {
        String password = "thisisapassword";

        PasswordField pwfield = lookup("#password").query();
        clickOn(pwfield).write(password);

        Node nextBtn = lookup("#loginBtn").query();
        clickOn(nextBtn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Repeat password field must not be empty", messageLabel.getText());
    }

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

    @Test
    public void SuccessfullPasswordCreation_ShouldShowInviteCodeScreen() {
        String password = "thisisapassword";

        PasswordField pwfield = lookup("#password").query();
        clickOn(pwfield).write(password);
        PasswordField pwRepeatfield = lookup("#passwordRepeat").query();
        clickOn(pwRepeatfield).write(password);

        Node btn = lookup("#loginBtn").query();
        clickOn(btn);

        Assert.assertEquals(LoginState.INVITE_CODE, spy.getState());
        Assert.assertNotNull(lookup("#UniqueInviteCode"));
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

//        DependencyInjectionSystem.setModule(Modules.override(
//                new DefaultWiringModule()).with(new AbstractModule() {
//            @Override
//            protected void configure() {
//                this.bind(Communication.class).toInstance(communication);
//            }
//        }));

        MasterLogin masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
        masterLogin.setState(LoginState.PASSWORD);
        spy = Mockito.spy(masterLogin);

        Mockito.doNothing().when(spy).updateRoot();
        spy.showMasterLoginScreen();
        Mockito.doCallRealMethod().when(spy).updateRoot();
        return spy.getController().getParentNode();
    }
}
