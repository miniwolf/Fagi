/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * This controller handles setup of username. Check whether the username
 * exists and calls MasterLogin when it has been verified.
 *
 * @author miniwolf
 */
public class CreateUserNameController implements LoginController {
    @FXML Label messageLabel;
    @FXML TextField username;
    @FXML Button loginBtn;
    private MasterLogin masterLogin;

    public CreateUserNameController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
    }

    @FXML
    public void initialize() {
        masterLogin.initialize(loginBtn);
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
        masterLogin.setUsername(username.getText());
        masterLogin.next();
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
