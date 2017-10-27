package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.framework.junit.ApplicationRule;

public class LoginTests {
    @Rule
    public ApplicationRule robot = new ApplicationRule((stage) -> {
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Scene scene = new Scene(new AnchorPane());
        stage.setScene(scene);
        Draggable draggable = new Draggable(stage);
        new MasterLogin(fagiApp, stage, draggable);
    });

    @Test
    public void WhenFocusedOnFieldAndTyping_TextIsContainedInFields() {
        String testText = "ThisTextShould Exist";
        Node usernameField = robot.lookup("#username").query();
        robot.clickOn(usernameField);

        Assert.assertEquals(((TextField) usernameField).getText(), testText);

        Node passwordField = robot.lookup("#password").query();
        robot.clickOn(passwordField).write(testText);
        Assert.assertEquals(((PasswordField) passwordField).getText(), testText);
    }
}
