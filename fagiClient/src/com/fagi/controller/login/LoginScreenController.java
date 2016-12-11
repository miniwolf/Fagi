/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.model.Login;
import com.fagi.network.ChatManager;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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
    @FXML private Button loginBtn;
    private MasterLogin masterLogin;

    public LoginScreenController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
    }

    @FXML
    private void initialize() {
        username.setText(masterLogin.getUsername());
        password.setText(masterLogin.getPassword());
        assignToLogin(username);
        assignToLogin(password);
    }

    private void assignToLogin(Node node) {
        node.setOnKeyPressed(event -> {
            if ( event.getCode() == KeyCode.ENTER ) {
                handleLogin();
            }
        });
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
