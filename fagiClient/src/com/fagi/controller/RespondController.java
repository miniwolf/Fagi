/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package com.fagi.controller;

import com.fagi.model.FriendRequest;
import com.fagi.network.ChatManager;

import javafx.stage.Stage;

public class RespondController {
    private Stage stage;
    private String requestName;

    public void handleOk() {
        ChatManager.handleFriendRequest(new FriendRequest(requestName));
        stage.close();
    }

    public void handleDelete() {
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
