/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller;

import com.fagi.model.Conversation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * @author miniwolf
 */
public class ConversationController {
    @FXML Label name;
    @FXML Label date;
    private Conversation conversation;

    public ConversationController(Conversation conversation) {
        this.conversation = conversation;
    }

    @FXML
    public void initialize() {
        name.setText(conversation.getName());
        date.setText(conversation.getDate());
    }
}
