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
import com.fagi.model.CreateUser;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class MasterController extends ScreenController {
    @FXML
    private Pane innerContent;
    @FXML
    protected Label messageLabel;
    protected boolean connected = false;

    public MasterController(FagiApp fagiApp, String configFileLocation, Stage primaryStage) {
        super(fagiApp, configFileLocation, primaryStage, null);
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
    public void handleQuit() {
        try {
            ChatManager.closeCommunication();
            fagiApp.stop();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @FXML
    public void initialize() {
        showScreen(ScreenType.LoginScreen);
    }

    public void showScreen(ScreenType screenType){
        System.out.println("Handling showscreen with type = " + screenType.name());
        try {
            String resourcePath = "";
            ScreenController controller = null;
            switch (screenType){
                case LoginScreen:
                    controller = new LoginScreen(this.fagiApp, this.configFileLocation, this.primaryStage, this);
                    resourcePath = "/com/fagi/view/LoginScreen.fxml";
                    break;
                case CreateUser:
                    controller = new CreateUserScreen(this.fagiApp, this.configFileLocation, this.primaryStage, this);
                    resourcePath = "/com/fagi/view/CreateUserScreen.fxml";
                    break;
            }
            if (!resourcePath.equals("") && controller != null) {
                FXMLLoader contentLoader = new FXMLLoader(controller.getClass().getResource(resourcePath));
                contentLoader.setController(controller);
                innerContent.getChildren().add(contentLoader.load());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
