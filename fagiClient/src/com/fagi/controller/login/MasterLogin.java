/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.controller.utility.Draggable;
import com.fagi.enums.LoginState;
import com.fagi.main.FagiApp;
import com.fagi.model.Login;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

/**
 * Controller class for the login screen, used by the JavaFX thread.
 * InitComponents and initCommunication will be called from the FagiApp
 * class.
 */
public class MasterLogin {
    private final FagiApp fagiApp;
    private final Communication communication;
    private final Draggable draggable;
    private final Stage stage;
    private WebEngine webEngine;
    private LoginController controller;
    private LoginState state = LoginState.LOGIN;
    private Login login;
    private String messageLabel;

    /**
     * Constructor will create and show the first screen.
     *
     * @param fagiApp   FagiApp used to login
     * @param draggable Passes the draggable from the stage
     */
    public MasterLogin(
            FagiApp fagiApp,
            Communication communication,
            Stage stage,
            Draggable draggable,
            WebEngine webEngine) {
        this.fagiApp = fagiApp;
        this.communication = communication;
        this.draggable = draggable;
        this.stage = stage;
        this.webEngine = webEngine;
    }

    public void showMasterLoginScreen() {
        showScreen(state);
        // THis is soo stupid...
        stage.sizeToScene();
        // TODO: Decide if this should be done before calling or this should be done inside here.
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
        setupController(screen);
        controller.initialize();
        controller.setMessage(messageLabel);
    }

    private void setupController(LoginState screen) {
        switch (screen) {
            case LOGIN:
                this.controller = new LoginScreenController(this, communication);
                break;
            case CREATE_USER:
                this.controller = new CreateUserController(this);
                break;
            default:
                throw new InvalidLoginState();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        draggable.mousePressed(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        draggable.mouseDragged(mouseEvent);
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login data) {
        login = data;
    }

    public LoginController getController() {
        return controller;
    }

    public void setMessageLabel(String messageLabel) {
        this.messageLabel = messageLabel;
        if (controller != null) {
            Platform.runLater(() -> controller.setMessage(messageLabel));
        }
    }

    public void setState(LoginState state) {
        this.state = state;
    }

    public LoginState getState() {
        return state;
    }
}
