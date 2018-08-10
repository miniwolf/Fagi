package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit.ApplicationTest;

public class InviteCodeTests extends ApplicationTest {
    private Communication communication;
    private MasterLogin masterLogin;

    @BeforeClass
    public static void initialize() {
        System.out.println("Starting InviteCodeTests");
    }

    @Test
    public void GivenInvalidInviteCode_MessageLabelShouldInformUserOfThis() {
        Mockito.when(communication.getNextResponse()).thenReturn(new IllegalInviteCode());

        String inviteCode = "41";

        Node field = lookup("#inviteCode").query();
        clickOn(field).write(inviteCode);

        Node btn = lookup("#loginBtn").query();
        clickOn(btn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals(LoginState.INVITE_CODE, masterLogin.getState());
        Assert.assertEquals("Error: Illegal invite code. Contact host", messageLabel.getText());
    }

    @Test
    public void InviteCodeMustHaveAValue_MessageLabelShouldInformOtherwise() {
        Node btn = lookup("#loginBtn").query();
        clickOn(btn);

        Label messageLabel = lookup("#messageLabel").query();

        Assert.assertEquals("Invite code cannot be empty", messageLabel.getText());
    }

    @Test
    public void GivenAValidInviteCode_TheLoginScreenShouldBeShown() {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        String inviteCode = "42";

        Node field = lookup("#inviteCode").query();
        clickOn(field).write(inviteCode);

        Node btn = lookup("#loginBtn").query();
        clickOn(btn);

        Assert.assertEquals(LoginState.LOGIN, masterLogin.getState());
        Assert.assertNotNull(lookup("#UniqueLoginScreen").query());
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
        masterLogin.setState(LoginState.INVITE_CODE);

        masterLogin.showMasterLoginScreen();

        masterLogin.setUsername("ThisIsAUsername");
        masterLogin.setPassword("ThisIsAPassword");

        stage.getScene().setRoot(masterLogin.getController().getParentNode());
        stage.show();
    }
}
