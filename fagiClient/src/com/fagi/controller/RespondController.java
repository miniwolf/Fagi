/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package com.fagi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.fagi.model.FriendRequest;
import com.fagi.network.ChatManager;

public class RespondController {
    @FXML private Label messageLabel;
    private Stage stage;
    private String requestName;

    @FXML
    private void handleOK() {
        ChatManager.handleFriendRequest(new FriendRequest(requestName));
        stage.close();
    }

    @FXML
    private void handleDelete() {
        ChatManager.handleRequestDelete(new FriendRequest(requestName));
        stage.close();
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
