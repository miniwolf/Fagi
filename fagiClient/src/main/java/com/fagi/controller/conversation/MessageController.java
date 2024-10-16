package com.fagi.controller.conversation;

import com.fagi.action.items.LoadFXML;
import com.fagi.util.FontUtils;
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

    public MessageController(
            String stringMessage,
            String resource) {
        this.stringMessage = stringMessage;
        new LoadFXML(resource).execute(this);
    }

    public MessageController(
            String stringMessage,
            String resource,
            String username) {
        this(stringMessage, resource);
        var image = new Image(
                "/style/material-icons/" + Character.toUpperCase(username.toCharArray()[0]) + ".png",
                32,
                32,
                true,
                true
        );
        this.image.setImage(image);
    }

    @FXML
    private void initialize() {
        message.setText(stringMessage);
        setupMessageSize();
    }

    private void setupMessageSize() {
        message.setPrefHeight(FontUtils.computeTextHeight(ROBOTO, stringMessage, 232) + 14.0);
        message.setPrefWidth(FontUtils.computeTextWidth(ROBOTO, stringMessage, 232) + 18.0);
    }
}
