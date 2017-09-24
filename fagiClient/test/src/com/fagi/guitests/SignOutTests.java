package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

public class SignOutTests extends GuiTest {
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
        Node usernameField = lookup("#username").query();
        Assert.assertNotNull(usernameField);
        Node passwordField = lookup("#password").query();
        Assert.assertNotNull(passwordField);
    }

    @Override
    protected Parent getRootNode() {
        Communication communication = Mockito.mock(Communication.class);
        Stage stage = (Stage) targetWindow();
        stage.setScene(new Scene(new AnchorPane()));
        ChatManager.setCommunication(communication);

        MainScreen test = new MainScreen("Test", communication, stage);
        test.initCommunication();
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Mockito.doAnswer(invocationOnMock ->
                                 new MasterLogin(fagiApp, "config/serverinfo.config", stage,
                                                 stage.getScene()))
               .when(fagiApp).showLoginScreen();
        ChatManager.setApplication(fagiApp);
        return test;
    }
}
