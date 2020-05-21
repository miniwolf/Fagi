package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.UserOnline;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assumptions;
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
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(ApplicationExtension.class)
public class LoginTests {
    private MasterLogin masterLogin;
    private Communication communication;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting login tests");
        FxAssert
                .assertContext()
                .setNodeFinder(new FagiNodeFinderImpl(FxService
                                                              .serviceContext()
                                                              .getWindowFinder()));
    }

    @Test
    public void WhenFocusedOnFieldAndTyping_TextIsContainedInFields(FxRobot robot) {
        var testText = "ThisTextShould Exist";

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", testText);

        FxAssert.verifyThat("#username", TextInputControlMatchers.hasText(testText));

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", testText);

        FxAssert.verifyThat("#password", TextInputControlMatchers.hasText(testText));
    }

    @Test
    public void WhenGivingWrongPassword_ErrorMessageShouldShowThis(FxRobot robot) {
        Mockito
                .when(communication.getNextResponse())
                .thenReturn(new PasswordError());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "dinMor");
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", "password");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat("#messageLabel", LabeledMatchers.hasText("Wrong password"));
    }

    @Test
    public void WhenGivingWrongUsername_ErrorMessageShouldShowThis(FxRobot robot) {
        Mockito
                .when(communication.getNextResponse())
                .thenReturn(new NoSuchUser());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "dinMor");
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", "password");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat("#messageLabel", LabeledMatchers.hasText("User doesn't exist"));
    }

    @Test
    public void WhenUserIsAlreadyOnline_ErrorMessageShouldShowThis(FxRobot robot) {
        Mockito
                .when(communication.getNextResponse())
                .thenReturn(new UserOnline());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "dinMor");
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#password", "password");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat("#messageLabel", LabeledMatchers.hasText("You are already online"));
    }

    @Test
    public void WhenCallingSetMessageLabel_NewMessageShouldAppear(FxRobot robot) {
        masterLogin.setMessageLabel("Connection refused");
        WaitForAsyncUtils.waitForFxEvents();

        FxAssert.verifyThat("#messageLabel", LabeledMatchers.hasText("Connection refused"));
    }

    @Test
    public void WhenClickingOnCreateNewUser_ShowCreateUsernameScreen(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOn(robot, "#newAccount");

        Assumptions.assumeTrue(LoginState.USERNAME.equals(masterLogin.getState()));
        FxAssert.verifyThat("#UniqueCreateUsernameView",
                            NodeMatchers.isNotNull(),
                            builder -> builder.append("Should switch to Create Username View.")
        );
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
        masterLogin.showMasterLoginScreen();

        stage
                .getScene()
                .setRoot(masterLogin
                                 .getController()
                                 .getParentNode());
        stage.show();
    }
}
