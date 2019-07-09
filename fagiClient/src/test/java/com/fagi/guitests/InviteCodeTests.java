package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxService;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
public class InviteCodeTests {
    private Communication communication;
    private MasterLogin masterLogin;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting InviteCodeTests");
        FxAssert.assertContext().setNodeFinder(new FagiNodeFinderImpl(FxService.serviceContext().getWindowFinder()));
    }

    @Test
    public void GivenInvalidInviteCode_MessageLabelShouldInformUserOfThis(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new IllegalInviteCode());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#inviteCode", "41");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        Assertions.assertEquals(LoginState.INVITE_CODE, masterLogin.getState());

        FxAssert.verifyThat(
                "#messageLabel",
                (Label messageLabel) -> messageLabel.getText().equals("Error: Illegal invite code. Contact host")
        );
    }

    @Test
    public void InviteCodeMustHaveAValue_MessageLabelShouldInformOtherwise(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat(
                "#messageLabel",
                (Label messageLabel) -> messageLabel.getText().equals("Invite code cannot be empty")
        );
    }

    @Test
    public void GivenAValidInviteCode_TheLoginScreenShouldBeShown(FxRobot robot) {
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#inviteCode", "42");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        Assertions.assertEquals(LoginState.LOGIN, masterLogin.getState());
        Assertions.assertTrue(
                robot.lookup("#UniqueLoginScreen").tryQuery().isPresent(),
                "The screen should return to login screen.");
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
        masterLogin.setState(LoginState.INVITE_CODE);

        masterLogin.showMasterLoginScreen();

        masterLogin.setUsername("ThisIsAUsername");
        masterLogin.setPassword("ThisIsAPassword");

        stage.getScene().setRoot(masterLogin.getController().getParentNode());
        stage.show();
    }
}
