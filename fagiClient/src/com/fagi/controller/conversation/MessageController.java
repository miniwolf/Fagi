package com.fagi.controller.conversation;

import com.fagi.action.items.LoadFXML;
import com.fagi.util.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * @author miniwolf
 */
public class MessageController extends HBox {
    @FXML private TextArea message;

    private final String stringMessage;
    private static final int MAX_COLUMN_LENGTH = 25;

    public MessageController(String stringMessage, String resource) {
        this.stringMessage = stringMessage;
        new LoadFXML(this, resource).execute();
    }

    @FXML
    private void initialize() {
        message.setText(stringMessage);
        setupMessageSize();
    }

    private void setupMessageSize() {
        Font roboto = new Font("Roboto-Regular", 13);
        message.setPrefHeight(Utils.computeTextHeight(roboto, stringMessage, 250) + 14.0);
        message.setPrefWidth(Utils.computeTextWidth(roboto, stringMessage, 250) + 18.0);
    }
}
