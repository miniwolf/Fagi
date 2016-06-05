package com.fagi.controller;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * LoginScreen.java
 *
 * Login screen for the IM-client part
 */

import com.fagi.config.ServerConfig;
import com.fagi.encryption.AES;
import com.fagi.encryption.RSA;
import com.fagi.encryption.RSAKey;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import main.FagiApp;

import java.io.IOException;
import java.security.KeyPair;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class LoginScreen {
    private final String configFileLocation;
    @FXML private Button loginBtn;
    @FXML private Label messageLabel;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField passwordRepeat;

    private boolean connected = false;
    private boolean creatingUser = false;
    private Stage primaryStage;
    private Application fagiApp;

    public LoginScreen(FagiApp fagiApp, String configFileLocation) {
        this.fagiApp = fagiApp;
        this.configFileLocation = configFileLocation;
    }

    public void initComponents() {
        loginBtn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) handleUserRequest();
        });
    }

    public void initCommunication() {
        try {
            ServerConfig config = ServerConfig.pathToServerConfig(configFileLocation);
            ChatManager.setCommunication(new Communication(config.getIp(), config.getPort(), new AES(), config.getServerKey()));
            messageLabel.setText("Connected to server: " + config.getName());
            connected = true;
        } catch (IOException ioe) {
            messageLabel.setText("Connection refused");
        } catch (ClassNotFoundException e) {
            messageLabel.setText("Not a valid config file.");
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

    public void menuQuit() {
        try {
            ChatManager.closeCommunication();
            fagiApp.stop();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
