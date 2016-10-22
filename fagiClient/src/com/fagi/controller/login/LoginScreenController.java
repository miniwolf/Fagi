/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.model.Login;
import com.fagi.network.ChatManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * Handles the login screen. Here it is possible to start creation process
 * of new account, or login.
 *
 * @author miniwolf
 */
public class LoginScreenController implements LoginController {
    @FXML private Label messageLabel;
    @FXML private TextField username;
    @FXML private PasswordField password;
    private MasterLogin masterLogin;

    public LoginScreenController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
    }

    @FXML
    public void initialize() {
        // TODO: Preinsert the values from username and password
    }

    @FXML
    public void handleLogin() {
        ChatManager.handleLogin(new Login(username.getText(), password.getText()), messageLabel);
    }

    @Override
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    public void next() {
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

    @Override
    public String getMessageLabel() {
        return messageLabel.getText();
    }
}
