package com.fagi.guitests;

import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

public class LoginTests extends GuiTest {
    @Override
    protected Parent getRootNode() {
        Stage stage = (Stage) targetWindow();
        stage.setScene(new Scene(new AnchorPane()));

        FagiApp fagiApp = Mockito.mock(FagiApp.class);

        Draggable draggable = new Draggable(stage);
        MasterLogin login = new MasterLogin(fagiApp, draggable, stage.getScene());
        return login.getController().getParent();
    }
}
