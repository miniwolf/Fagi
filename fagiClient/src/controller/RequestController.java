package controller;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.FriendRequest;
import network.ChatManager;


public class RequestController {
    @FXML private TextField username;
    @FXML private Button sendButton;

    private Stage stage;

    /**
     * Called when the user press Send Request
     */
    @FXML
    private void handleButton() {
        ChatManager.handleFriendRequest(new FriendRequest(username.getText()));
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
