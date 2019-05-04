/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.action.items.LoadHTML;
import com.fagi.network.ChatManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * This controller handles setup of username. Check whether the username
 * exists and calls MasterLogin when it has been verified.
 *
 * @author miniwolf
 */
public class CreateUserController extends DefaultLoginController {
    @FXML private TextField username;

    public CreateUserController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
        new LoadHTML(this, engine, "/com/fagi/view/login/CreateUserName.fxml").execute();
    }

    @FXML
    private void initialize() {
        username.setText(masterLogin.getUsername());
        masterLogin.initialize(username);
        Platform.runLater(() -> username.getParent().requestFocus());
    }

    @Override
    public void next() {
        if (username.getText() == null || "".equals(username.getText())) {
            messageLabel.setText("Username cannot be empty");
            return;
        }
        boolean valid = ChatManager.isValidUserName(username.getText());

        if (!valid) {
            messageLabel.setText("Username may not contain special symbols");
            return;
        }

        boolean available = ChatManager.checkIfUserNameIsAvailable(username.getText());

        if (available) {
            masterLogin.setUsername(username.getText());
            masterLogin.next();
        } else {
            messageLabel.setText("Username is not available");
        }
    }
}