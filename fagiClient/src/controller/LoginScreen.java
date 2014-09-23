package controller;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * LoginScreen.java
 *
 * Login screen for the IM-client part
 */

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import model.Login;
import network.ChatManager;
import network.Communication;

import java.io.IOException;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class LoginScreen {
    @FXML private Button loginBtn;
    @FXML private Label messageLabel;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField passwordRepeat;

    private boolean connected = false, creatingUser = false;
    private Stage primaryStage;

    public void initComponents() {
        loginBtn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleUserRequest();
        });
    }

    public void initCommunication() {
        try {
            ChatManager.setCommunication(new Communication());
            messageLabel.setText("Connected to server");
            connected = true;
        } catch (IOException ioe) {
            messageLabel.setText("Connection refused");
        }
    }

    @FXML
    void menuCreateUserActionPerformed() {
        if ( !connected ) {
            messageLabel.setText("Cannot create user, no connection");
            return;
        }

        passwordRepeat.setVisible(true);
        loginBtn.setText("Create User");
        creatingUser = true;
        primaryStage.sizeToScene();
    }

    @FXML
    void handleUserRequest() {
        if ( !connected ) {
            messageLabel.setText("Cannot handle user requests, no connection"); return;
        }

        if ( creatingUser ) {
            ChatManager.handleCreateUser(username.getText(), password.getText(), passwordRepeat.getText(), messageLabel);
            passwordRepeat.setVisible(false);
            loginBtn.setText("Login");
            creatingUser = false;
            primaryStage.sizeToScene();
        } else {
            String username = this.username.getText();
            Login login = new Login(username, password.getText());
            ChatManager.handleLogin(login, username, messageLabel);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
