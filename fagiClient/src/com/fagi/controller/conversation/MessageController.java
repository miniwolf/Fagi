package com.fagi.controller.conversation;

import com.fagi.action.items.LoadFXML;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author miniwolf
 */
public class MessageController extends HBox {
    @FXML private Label message;

    private final String stringMessage;

    public MessageController(String stringMessage, String resource) {
        this.stringMessage = stringMessage;
        new LoadFXML(this, resource).execute();
    }

    @FXML
    private void initialize() {
        message.setText(stringMessage);
    }
}
