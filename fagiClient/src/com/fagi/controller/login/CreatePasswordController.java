/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.action.items.LoadFXML;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

/**
 * Collects the password from the entered user details
 *
 * @author miniwolf
 */
public class CreatePasswordController extends DefaultLoginController {
    @FXML private PasswordField password;
    @FXML private PasswordField passwordRepeat;

    public CreatePasswordController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
        new LoadFXML(this, "/com/fagi/view/login/CreatePassword.fxml").execute();
    }

    @FXML
    private void initialize() {
        masterLogin.initialize(passwordRepeat);
    }

    @Override
    public void next() {
        if (password.getText() == null || password.getText().equals("")) {
            messageLabel.setText("Password field must not be empty");
            return;
        }

        if (passwordRepeat.getText() == null || passwordRepeat.getText().equals("")) {
            messageLabel.setText("Repeat password field must not be empty");
            return;
        }

        if (!password.getText().equals(passwordRepeat.getText())) {
            messageLabel.setText("Passwords does not match");
            return;
        }
        masterLogin.setPassword(password.getText());
        masterLogin.next();
    }
}
