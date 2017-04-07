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
        Image image = new Image("/com/fagi/style/material-icons/"
                                + Character.toUpperCase(username.toCharArray()[0])
                                + ".png", 32, 32, true, true);
        this.image.setImage(image);
    }

    @FXML
    private void initialize() {
        message.setText(stringMessage);
        setupMessageSize();
    }

    private void setupMessageSize() {
        double assumedHeight = Utils.computeTextHeight(ROBOTO, stringMessage, 232);
        message.setPrefHeight(assumedHeight + 14.0);
        message.setPrefWidth(Utils.computeTextWidth(ROBOTO, stringMessage, 232) + 18.0);
    }
}
