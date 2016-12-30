/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.action.items.LoadFXML;
import com.fagi.network.ChatManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * This controller handles setup of username. Check whether the username
 * exists and calls MasterLogin when it has been verified.
 *
 * @author miniwolf
 */
public class CreateUserNameController extends Pane implements LoginController {
    @FXML private Label messageLabel;
    @FXML private TextField username;
    @FXML private Button loginBtn;

    private MasterLogin masterLogin;

    public CreateUserNameController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
        new LoadFXML(this, "/com/fagi/view/login/CreateUserName.fxml").execute();
    }

    @FXML
    private void initialize() {
        username.setText(masterLogin.getUsername());
        masterLogin.initialize(username);
        Platform.runLater(() -> username.getParent().requestFocus());
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
    public void next() {
        if ("".equals(username.getText())) {
            messageLabel.setText("Username cannot be empty");
            return;
        }

        boolean available = ChatManager.checkIfUserNameIsAvailable(username.getText());
        boolean valid = ChatManager.isValidUserName(username.getText());

        if (available && valid) {
            masterLogin.setUsername(username.getText());
            masterLogin.next();
        } else if (!valid) {
            messageLabel.setText("Username may not contain special symbols");
        } else {
            messageLabel.setText("Username is not available");
        }
    }

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
}
