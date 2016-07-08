/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * LoginScreen.java
 *
 * Login screen for the IM-client part
 */

package com.fagi.controller;

import com.fagi.enums.ScreenType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.main.FagiApp;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class LoginScreen extends ScreenController {
    @FXML private Button loginBtn;
    @FXML private TextField username;
    @FXML private PasswordField password;

    public LoginScreen(FagiApp fagiApp, String configFileLocation, Stage primaryStage, MasterController parent) {
        super(fagiApp, configFileLocation, primaryStage, parent);
    }

    @FXML
    public void initialize() {
        loginBtn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleUserRequest();
            }
        });
    }

    @FXML
    public void handleCreateUser() {
        if (!parentController.connected) {
            return;
        }
        primaryStage.sizeToScene();
        parentController.showScreen(ScreenType.CreateUser);
    }

    @FXML
    public void handleUserRequest() {
        if (!parentController.connected) {
            return;
        }
        String username = this.username.getText();
        Login login = new Login(username, password.getText());
        ChatManager.handleLogin(login, username, parentController.messageLabel);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
