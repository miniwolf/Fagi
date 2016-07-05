/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * LoginScreen.java
 *
 * Login screen for the IM-client part
 */

package com.fagi.controller;

import com.fagi.config.ServerConfig;
import com.fagi.controller.utility.Draggable;
import com.fagi.encryption.AES;
import com.fagi.encryption.RSA;
import com.fagi.encryption.RSAKey;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.main.FagiApp;

import java.io.IOException;
import java.security.KeyPair;

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

    private final String configFileLocation;
    private boolean connected = false;
    private boolean creatingUser = false;
    private Stage primaryStage;
    private Application fagiApp;
    private final Draggable draggable;

    public LoginScreen(FagiApp fagiApp, String configFileLocation, Stage primaryStage) {
        this.fagiApp = fagiApp;
        this.configFileLocation = configFileLocation;
        draggable = new Draggable(primaryStage);
    }

    @FXML
    public void initialize() {
        loginBtn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleUserRequest();
            }
        });
    }

    /**
     * Setting up communication with the server, this is created when the UI has been loaded.
     * This means that one should never call this before initComponents.
     * Function calls in this method depends on the initComponents.
     */
    public void initCommunication() {
        try {
            ServerConfig config = ServerConfig.pathToServerConfig(configFileLocation);
            AES aes = new AES();
            aes.generateKey(128);
            ChatManager.setCommunication(new Communication(config.getIp(), config.getPort(), aes, config.getServerKey()));
            messageLabel.setText("Connected to server: " + config.getName());
            connected = true;
        } catch (IOException ioe) {
            messageLabel.setText("Connection refused");
        } catch (ClassNotFoundException e) {
            messageLabel.setText("Not a valid config file.");
        }
    }

    @FXML
    public void handleCreateUser() {
        if ( !connected ) {
            return;
        }

        passwordRepeat.setVisible(true);
        loginBtn.setText("Create User");
        creatingUser = true;
        primaryStage.sizeToScene();
    }

    @FXML
    public void handleUserRequest() {
        if ( !connected ) {
            return;
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

    @FXML
    public void handleQuit() {
        try {
            ChatManager.closeCommunication();
            fagiApp.stop();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @FXML
    public void mousePressed(MouseEvent mouseEvent) {
        draggable.mousePressed(mouseEvent);
    }

    @FXML
    public void mouseDragged(MouseEvent mouseEvent) {
        draggable.mouseDragged(mouseEvent);
    }
}
