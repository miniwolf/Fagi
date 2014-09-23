/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.FriendRequest;
import network.ChatManager;

public class RespondController {
    @FXML private Label messageLabel;
    private Stage stage;
    private String username;

    @FXML
    private void handleOK() {
        ChatManager.handleFriendRequest(new FriendRequest(username));
        stage.close();
    }

    @FXML
    private void handleDelete() {
        // TODO: Enable deletion of friends.
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
