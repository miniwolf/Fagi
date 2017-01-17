package com.fagi.controller.conversation;

import com.fagi.action.items.LoadFXML;
import com.fagi.util.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * @author miniwolf
 */
public class MessageController extends HBox {
    @FXML private TextArea message;
    @FXML private ImageView image;

    private final String stringMessage;
    private static final Font ROBOTO = new Font("Roboto-Regular", 13);

    public MessageController(String stringMessage, String resource) {
        this.stringMessage = stringMessage;
        new LoadFXML(this, resource).execute();
    }

    public MessageController(String stringMessage, String resource, String username) {
        this.stringMessage = stringMessage;
        new LoadFXML(this, resource).execute();
        image.setImage(new Image("/com/fagi/style/material-icons/" + username.toCharArray()[0] + ".png"));
    }

    @FXML
    private void initialize() {
        message.setText(stringMessage);
        setupMessageSize();
    }

    private void setupMessageSize() {
        message.setPrefHeight(Utils.computeTextHeight(ROBOTO, stringMessage, 250) + 14.0);
        message.setPrefWidth(Utils.computeTextWidth(ROBOTO, stringMessage, 250) + 18.0);
    }
}
