package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

public class LoginTests extends GuiTest {
    @Test
    public void WhenFocusedOnFieldAndTyping_TextIsContainedInFields() {
        String testText = "ThisTextShould Exist";
        Node usernameField = lookup("#username").query();
        clickOn(usernameField).write(testText);

        Assert.assertEquals(((TextField) usernameField).getText(), testText);

        Node passwordField = lookup("#password").query();
        clickOn(passwordField).write(testText);
        Assert.assertEquals(((PasswordField) passwordField).getText(), testText);
    }

    @Override
    protected Parent getRootNode() {
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Stage stage = (Stage) targetWindow();
        stage.setScene(new Scene(new AnchorPane()));
        Draggable draggable = new Draggable(stage);
        MasterLogin masterLogin = new MasterLogin(fagiApp, stage, draggable);
        return masterLogin.getController().getParentNode();
    }
}
