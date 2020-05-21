package com.fagi.controller.login;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Default implementation of LoginController.
 * <p>
 * The thing left is the next, which should be declared by each specific implementation
 * in order to validate data before send on to the ChatManager.
 *
 * @author miniwolf
 */
public abstract class DefaultLoginController extends Pane implements LoginController {
    @FXML protected Label messageLabel;
    @FXML protected Button loginBtn;

    MasterLogin masterLogin;

    @Override
    public void back() {
        masterLogin.back();
    }

    @Override
    public void handleQuit() {
        masterLogin.handleQuit();
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        masterLogin.mousePressed(mouseEvent);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        masterLogin.mouseDragged(mouseEvent);
    }

    @Override
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public String getMessageLabel() {
        return messageLabel.getText();
    }

    @Override
    public Parent getParentNode() {
        return this;
    }
}
