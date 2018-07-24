package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
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
        Node query = lookup("#UniqueLoginScreen").query();
        Assert.assertNotNull(query);
    }

    @Override
    protected Parent getRootNode() {
        System.out.println("Starting SignOut tests");
        Stage stage = (Stage) targetWindow();
        Scene scene = new Scene(new AnchorPane());
        stage.setScene(scene);
        Communication communication = Mockito.mock(Communication.class);
        Mockito.when(communication.getNextResponse()).thenReturn(new AllIsWell());
        FagiApp fagiApp = Mockito.mock(FagiApp.class);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

//        DependencyInjectionSystem.setModule(Modules.override(
//                new DefaultWiringModule()).with(new AbstractModule() {
//            @Override
//            protected void configure() {
//                this.bind(Communication.class).toInstance(communication);
//            }
//        }));

        MainScreen test = new MainScreen("Test", communication, stage);
        test.initCommunication();

        Draggable draggable = new Draggable(stage);
        Mockito.doAnswer(invocationOnMock -> {
            MasterLogin masterLogin = new MasterLogin(fagiApp, communication, stage, draggable);
            masterLogin.showMasterLoginScreen();
            scene.setRoot(masterLogin.getController().getParentNode());
            stage.setScene(scene);
            stage.showAndWait();
            return masterLogin;
        }).when(fagiApp).showLoginScreen();
        return test;
    }
}
