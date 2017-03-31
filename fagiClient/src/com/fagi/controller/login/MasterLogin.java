/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.config.ServerConfig;
import com.fagi.controller.utility.Draggable;
import com.fagi.encryption.AES;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;

import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class MasterLogin {
    private String username;
    private LoginController controller;

    private String messageLabel;

    private LoginState state = LoginState.LOGIN;

    private final FagiApp fagiApp;
    private final String configFileLocation;
    private final Draggable draggable;
    private final Scene scene;
    private String password;
    private String inviteCode;

    /**
     * Constructor will create and show the first screen.
     *
     * @param fagiApp            FagiApp used to login
     * @param configFileLocation Location for the config file
     * @param primaryStage       Stage to show the scene
     * @param scene              scene to add content
     */
    public MasterLogin(FagiApp fagiApp, String configFileLocation,
                       Stage primaryStage, Scene scene) {
        this.fagiApp = fagiApp;
        this.configFileLocation = configFileLocation;
        draggable = new Draggable(primaryStage);
        this.scene = scene;

        showScreen(state);
        initCommunication();
        primaryStage.sizeToScene();
    }

    /**
     * Setting up communication with the server, this is created when the UI has been loaded.
     * This means that one should never call this before initComponents.
     * Function calls in this method depends on the initComponents.
     */
    private void initCommunication() {
        try {
            ServerConfig config = ServerConfig.pathToServerConfig(configFileLocation);
            AES aes = new AES();
            aes.generateKey(128);
            ChatManager.setCommunication(new Communication(config.getIp(), config.getPort(), aes,
                                                           config.getServerKey()));
            messageLabel = "Connected to server: " + config.getName();
        } catch (IOException ioe) {
            messageLabel = "Connection refused";
        } catch (ClassNotFoundException cnfe) {
            messageLabel = "Not a valid config file.";
        }
    }

    /**
     * Will close communication and quick the application.
     */
    public void handleQuit() {
        try {
            ChatManager.closeCommunication();
            fagiApp.stop();
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
    }

    /**
     * Assigns the Enter button as a shortcut for the next method in the controller.
     *
     * @param node Node works as a placeholder for the eventlistener
     */
    public void initialize(Node node) {
        node.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                controller.next();
            }
        });
    }

    /**
     * Will switch the state of the process and update the view with the new controller.
     */
    public void next() {
        switch (state) {
            case LOGIN:
                state = LoginState.USERNAME;
                break;
            case USERNAME:
                state = LoginState.PASSWORD;
                break;
            case PASSWORD:
                state = LoginState.INVITE_CODE;
                break;
            case INVITE_CODE:
                state = LoginState.LOGIN;
                break;
            default:
                System.out.println(state + " is not known");
                throw new UnsupportedOperationException();
        }
        showScreen(state);
    }

    /**
     * Undoes the the next action unless you are at the login screen where it is not possible
     * to go further back.
     */
    public void back() {
        switch (state) {
            case LOGIN:
                break;
            case USERNAME:
                state = LoginState.LOGIN;
                break;
            case PASSWORD:
                state = LoginState.USERNAME;
                break;
            case INVITE_CODE:
                state = LoginState.PASSWORD;
                break;
            default:
                System.out.println(state + " is not known");
                throw new UnsupportedOperationException();
        }
        showScreen(state);
    }

    private void showScreen(LoginState screen) {
        if (!setupController(screen)) {
            return;
        }
        controller.setMessage(messageLabel);
    }

    private boolean setupController(LoginState screen) {
        // TODO: Rewrite this into proper interface usage
        LoginController controller;
        switch (screen) {
            case LOGIN:
                controller = new LoginScreenController(this);
                break;
            case USERNAME:
                controller = new CreateUserNameController(this);
                break;
            case PASSWORD:
                controller = new CreatePasswordController(this);
                break;
            case INVITE_CODE:
                controller = new InviteCodeController(this);
                break;
            default:
                return false;
        }
        setController((Parent) controller, controller);
        return true;
    }

    private void setController(Parent pane, LoginController controller) {
        scene.setRoot(pane);
        this.controller = controller;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        draggable.mousePressed(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        draggable.mouseDragged(mouseEvent);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Object getController() {
        return controller;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getInviteCode() {
        return inviteCode;
    }
}
