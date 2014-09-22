package model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Chat.java
 *
 * Tool class for holding text for conversations.
 */

import javafx.scene.control.TextArea;

public class Chat extends TextArea {
    private final String chatBuddy;

    public Chat(String chatBuddy) {
        this.chatBuddy = chatBuddy;
        initComponents();
    }

    private void initComponents() {
        setPrefColumnCount(20);
        setPrefRowCount(5);
        setEditable(false);
        setWrapText(true);
        setPrefHeight(59);
        setPrefWidth(20);
        setMinSize(1, 20);
        setText("You are now chatting with: " + chatBuddy + "\n");
    }
}