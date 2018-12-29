package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.concurrent.TimeoutException;

@ExtendWith(ApplicationExtension.class)
public class SignOutTests {
    @BeforeAll
    public static void initialize() {
        System.out.println("Starting SignOutTests");
        try {
            FxToolkit.registerPrimaryStage();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clickOnToggleSignOut_WillChangeVisibilityOnDropdownId(FxRobot robot) {
        Node signOutPane = robot.lookup("#dropdown").query();
        Assertions.assertFalse(signOutPane.isVisible());
        Node toggleSignOut = robot.lookup(".gb_b").query();
        robot.clickOn(toggleSignOut);
        Assertions.assertTrue(signOutPane.isVisible());
        robot.clickOn(toggleSignOut);
        Assertions.assertFalse(signOutPane.isVisible());
    }

    @Test
    public void clickOnSignOut_WillStartTheLoginScreen(FxRobot robot) {
        Node toggleSignOut = robot.lookup(".gb_b").query();
        robot.clickOn(toggleSignOut);
        Node logoutButton = robot.lookup(".gb_Fa").query();
        robot.clickOn(logoutButton);
        // This is supposed to check that we are on the loginscreen
        Node query = robot.lookup("#UniqueLoginScreen").query();
        Assertions.assertNotNull(query);
    }

    @Start
    public void start(Stage stage) {
        Communication communication = Mockito.mock(Communication.class);
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());
        FagiApp fagiApp = Mockito.mock(FagiApp.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        MainScreen test = new MainScreen("Test", communication, stage);
        test.initCommunication();

        Draggable draggable = new Draggable(stage);
        Scene scene = new Scene(test);
        Mockito.doAnswer(invocationOnMock -> {
            stage.setScene(scene);
            MasterLogin masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
            masterLogin.showMasterLoginScreen();
            stage.show();
            return masterLogin;
        }).when(fagiApp).showLoginScreen();
        stage.setScene(scene);
        stage.show();
    }
}
