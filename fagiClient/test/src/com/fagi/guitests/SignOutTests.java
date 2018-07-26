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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.TimeoutException;

public class SignOutTests extends ApplicationTest {
    @BeforeClass
    public static void setUpClass() {
        try {
            FxToolkit.registerPrimaryStage();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clickOnToggleSignOut_WillChangeVisibilityOnDropdownId() {
        Node signOutPane = lookup("#dropdown").query();
        Assert.assertFalse(signOutPane.isVisible());
        Node toggleSignOut = lookup(".gb_b").query();
        clickOn(toggleSignOut);
        Assert.assertTrue(signOutPane.isVisible());
        clickOn(toggleSignOut);
        Assert.assertFalse(signOutPane.isVisible());
    }

    @Test
    public void clickOnSignOut_WillStartTheLoginScreen() {
        Node toggleSignOut = lookup(".gb_b").query();
        clickOn(toggleSignOut);
        Node logoutButton = lookup(".gb_Fa").query();
        clickOn(logoutButton);
        // This is supposed to check that we are on the loginscreen
        Node query = lookup("#UniqueLoginScreen").query();
        Assert.assertNotNull(query);
    }

    @Override
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
