package com.fagi.controller.conversation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author miniwolf
 */
public class MessageController {
    @FXML private Label message;

    private final String stringMessage;

    public MessageController(String stringMessage) {
        this.stringMessage = stringMessage;
    }

    @FXML
    public void initialize() {
        message.setText(stringMessage);
    }
}
