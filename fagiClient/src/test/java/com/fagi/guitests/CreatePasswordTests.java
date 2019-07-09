package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
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
import org.testfx.matcher.base.NodeMatchers;

@ExtendWith(ApplicationExtension.class)
public class CreatePasswordTests {
    private MasterLogin masterLogin;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting CreatePasswordTests");
        FxAssert.assertContext().setNodeFinder(new FagiNodeFinderImpl(FxService.serviceContext().getWindowFinder()));
    }

    @Test
    public void PasswordFieldMustHaveAValue_MessageShouldIndicateOtherwise(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat(
                "#messageLabel",
                (Label label) -> label.getText().equals("Password field must not be empty")
        );
    }

    @Test
    public void PasswordRepeatFieldMustHaveAValue_MessageShouldIndicateOtherwise(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", "thisisapassword");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat(
                "#messageLabel",
                (Label label) -> label.getText().equals("Repeat password field must not be empty")
        );
    }

    @Test
    public void RepeatPasswordMustMatchPassword_ErrorInformUser(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", "thisisapassword");
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#passwordRepeat", "thisisadiffrentpassword");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat(
                "#messageLabel",
                (Label label) -> label.getText().equals("Passwords does not match")
        );
    }

    @Test
    public void SuccessfulPasswordCreation_ShouldShowInviteCodeScreen(FxRobot robot) {
        var password = "thisisapassword";

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", password);
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#passwordRepeat", password);
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        Assertions.assertEquals(LoginState.INVITE_CODE, masterLogin.getState());
        FxAssert.verifyThat(
                "#UniqueInviteCode",
                NodeMatchers.isNotNull()
        );
    }

    @Start
    public void start(Stage stage) {
        var fagiApp = Mockito.mock(FagiApp.class);
        var draggable = new Draggable(stage);

        var communication = Mockito.mock(Communication.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
        masterLogin.setState(LoginState.PASSWORD);
        masterLogin.showMasterLoginScreen();

        stage.getScene().setRoot(masterLogin.getController().getParentNode());
        stage.show();
    }
}
