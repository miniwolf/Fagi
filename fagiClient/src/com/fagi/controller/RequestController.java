package com.fagi.controller;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.fagi.model.FriendRequest;
import com.fagi.network.ChatManager;


public class RequestController {
    @FXML private TextField username;
    @FXML private Button sendButton;

    private Stage stage;

    /**
     * Called when the user press Send Request.
     */
    public void handleButton() {
        ChatManager.handleFriendRequest(new FriendRequest(username.getText()));
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
