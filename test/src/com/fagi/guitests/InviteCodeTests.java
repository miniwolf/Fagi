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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class InviteCodeTests {
    private Communication communication;
    private MasterLogin masterLogin;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting InviteCodeTests");
    }

    @Test
    public void GivenInvalidInviteCode_MessageLabelShouldInformUserOfThis(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new IllegalInviteCode());

        String inviteCode = "41";

        Node field = robot.lookup("#inviteCode").query();
        robot.clickOn(field).write(inviteCode);

        Node btn = robot.lookup("#loginBtn").query();
        robot.clickOn(btn);

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals(LoginState.INVITE_CODE, masterLogin.getState());
        Assertions.assertEquals("Error: Illegal invite code. Contact host", messageLabel.getText());
    }

    @Test
    public void InviteCodeMustHaveAValue_MessageLabelShouldInformOtherwise(FxRobot robot) {
        Node btn = robot.lookup("#loginBtn").query();
        robot.clickOn(btn);

        Label messageLabel = robot.lookup("#messageLabel").query();

        Assertions.assertEquals("Invite code cannot be empty", messageLabel.getText());
    }

    @Test
    public void GivenAValidInviteCode_TheLoginScreenShouldBeShown(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        String inviteCode = "42";

        Node field = robot.lookup("#inviteCode").query();
        robot.clickOn(field).write(inviteCode);

        Node btn = robot.lookup("#loginBtn").query();
        robot.clickOn(btn);

        Assertions.assertEquals(LoginState.LOGIN, masterLogin.getState());
        Assertions.assertNotNull(robot.lookup("#UniqueLoginScreen").query());
    }

    @Start
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
