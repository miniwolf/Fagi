/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.action.items.LoadFXML;
import com.fagi.login.LoginResultHandler;
import com.fagi.login.LoginSystem;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.responses.Response;
import com.google.inject.Injector;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Handles the login screen. Here it is possible to start creation process
 * of new account, or login.
 *
 * @author miniwolf
 */
public class LoginScreenController extends Pane implements LoginController {
    @FXML Label messageLabel;
    @FXML TextField username;
    @FXML PasswordField password;
    @FXML Button loginBtn;
    private MasterLogin masterLogin;

    public LoginScreenController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
        new LoadFXML(this, "/com/fagi/view/login/LoginScreen.fxml").execute();
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
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin();
            }
        });
    }

    @FXML
    private void handleLogin() {
        Login data = new Login(username.getText(), password.getText());
        Response loginResponse = new LoginSystem().login(data);
        new LoginResultHandler().handle(loginResponse, data.getUsername(), messageLabel);
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
    public String getMessageLabel() {
        return messageLabel.getText();
    }

    @Override
    public Parent getParentNode() {
        return this;
    }
}
