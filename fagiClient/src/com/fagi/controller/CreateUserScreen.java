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
import com.fagi.enums.ScreenType;
import com.fagi.main.FagiApp;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class CreateUserScreen extends ScreenController {
    @FXML private Button loginBtn;
    @FXML private TextField username;
    @FXML private PasswordField password;
    @FXML private PasswordField passwordRepeat;

    public CreateUserScreen(FagiApp fagiApp, String configFileLocation, Stage primaryStage, MasterController parent) {
        super(fagiApp, configFileLocation, primaryStage, parent);
    }

    @FXML
    public void initialize() {
        loginBtn.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleCreateUserRequest();
            }
        });
    }


    @FXML
    public void handleLoginUser(){
        System.out.println("Returning to login screen");
        primaryStage.sizeToScene();
        parentController.showScreen(ScreenType.LoginScreen);
    }

    @FXML
    public void handleCreateUserRequest() {
        if (!parentController.connected) {
            return;
        }
        System.out.println("Creating user");
        boolean isCreated = ChatManager.handleCreateUser(username.getText(), password.getText(), passwordRepeat.getText(), parentController.messageLabel);
        if (isCreated) {
            primaryStage.sizeToScene();
            parentController.showScreen(ScreenType.LoginScreen);
        }
    }
}
