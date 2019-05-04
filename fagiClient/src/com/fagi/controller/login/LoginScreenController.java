/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.action.items.LoadHTML;
import com.fagi.controller.MissingElement;
import com.fagi.login.CheckUsername;
import com.fagi.login.LoginResultHandler;
import com.fagi.login.LoginSystem;
import com.fagi.model.Login;
import com.fagi.network.Communication;
import com.fagi.responses.Response;
import javafx.concurrent.Worker;
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
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import java.net.URL;
import java.util.function.Function;

/**
 * Handles the login screen. Here it is possible to start creation process
 * of new account, or login.
 *
 * @author miniwolf
 */
public class LoginScreenController extends Pane implements LoginController {
    private static final String resourcePath = "/com/fagi/view/login/LoginScreen.html";
    Label messageLabel;
    Element username;
    private MasterLogin masterLogin;
    private final Communication communication;
    Element password;
    private WebEngine engine;

    public LoginScreenController(
            MasterLogin masterLogin,
            Communication communication) {
        this.masterLogin = masterLogin;
        this.communication = communication;
    }

    private void initialize(WebEngine engine) throws MissingElement {
        this.engine = engine;

        new LoadHTML(this, engine, resourcePath).execute();

        engine.getLoadWorker().stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                var document = engine.getDocument();
                username = document.getElementById("identifierId");
                password = document.getElementById("identifierHiddenPassword");
            }
        });
        if (username == null) {
            throw new MissingElement("identifierId", resourcePath);
        }
        username.setTextContent(masterLogin.getLogin().getUsername());
        if (password == null) {
            throw new MissingElement("identifierHiddenPassword", resourcePath);
        }
        password.setTextContent(masterLogin.getLogin().getPassword());
    }

    public void handleLogin() {
        Login data = new Login(username.getTextContent(), password.getTextContent());
        masterLogin.setLogin(data);
        Response loginResponse = new LoginSystem(communication).login(data);
        new LoginResultHandler(communication).handle(loginResponse, data.getUsername(), messageLabel);
    }

    public void checkUsername(String username) {
        var result = new CheckUsername(username, communication).checkValidUsername();
        switch (result) {
            case Valid:

                break;
            default:
        }
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
