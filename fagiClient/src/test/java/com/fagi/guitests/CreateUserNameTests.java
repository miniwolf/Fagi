package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
public class CreateUserNameTests {
    private Communication communication;
    private MasterLogin masterLogin;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting CreateUserNameTests");
        FxAssert
                .assertContext()
                .setNodeFinder(new FagiNodeFinderImpl(FxService
                                                              .serviceContext()
                                                              .getWindowFinder()));
    }

    @Test
    public void ThereMustBeAValueInTheUsernameField_TheMessageLabelShouldInformOtherwise(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat("#messageLabel",
                            (Label messageLabel) -> messageLabel
                                    .getText()
                                    .equals("Username cannot be empty")
        );
    }

    @Test
    public void WhenUserExistsOnServerCreatingANewUser_InformNewUser(FxRobot robot) {
        Mockito
                .when(communication.getNextResponse())
                .thenReturn(new UserExists());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "DinMor");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat("#messageLabel",
                            (Label messageLabel) -> messageLabel
                                    .getText()
                                    .equals("Username is not available")
        );
    }

    @Test
    public void UsernameMustNotContainAnySpecialCharacters_InformNemUser(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "Din Mor");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        FxAssert.verifyThat("#messageLabel",
                            (Label messageLabel) -> messageLabel
                                    .getText()
                                    .equals("Username may not contain special symbols")
        );
    }

    @Test
    public void UsernameIsValidAndAvailable_ShowsCreatePasswordScreen(FxRobot robot) {
        Mockito
                .when(communication.getNextResponse())
                .thenReturn(new AllIsWell());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "username");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");

        Assertions.assertEquals(LoginState.PASSWORD, masterLogin.getState());
        FxAssert.verifyThat("#passwordRepeat", NodeMatchers.isNotNull());
    }

    @Test
    public void ClickingOnBackButton_ResultsInReturningToLoginScreen(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "username");
        WaitForFXEventsTestHelper.clickOn(robot, "#backBtn");

        FxAssert.verifyThat("#UniqueLoginScreen", NodeMatchers.isNotNull());
    }

    @Test
    public void WritingUsernameClickingOnBackButtonAndGoingBackToCreateUsername_ResultsInUsernameFieldCleared(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "username");
        WaitForFXEventsTestHelper.clickOn(robot, "#backBtn");
        WaitForFXEventsTestHelper.clickOn(robot, "#newAccount");

        FxAssert.verifyThat("#username", (TextField usernameTextField) -> usernameTextField.getText() == null);
    }

    @Test
    public void GoingBackFromCreatePassword_ShouldKeepUsername(FxRobot robot) {
        Mockito
                .when(communication.getNextResponse())
                .thenReturn(new AllIsWell());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#username", "username");
        WaitForFXEventsTestHelper.clickOn(robot, "#loginBtn");
        WaitForFXEventsTestHelper.clickOn(robot, "#backBtn");

        FxAssert.verifyThat("#username", (TextField usernameTextField) -> usernameTextField.getText() != null);
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
        masterLogin.setState(LoginState.USERNAME);
        masterLogin.showMasterLoginScreen();

        stage
                .getScene()
                .setRoot(masterLogin
                                 .getController()
                                 .getParentNode());
        stage.show();
    }
}
