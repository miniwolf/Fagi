package com.fagi.model;/*
 * Copyright (c) 2011. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Conversation.java
 *
 * Tool class contains information on name and textarea for a chat.
 */

/**
 *  TODO: Write description
 */
public class Conversation {
    private final String chatBuddy;
    private final Chat chat;
    private String name;
    private String date;

    public Conversation(String chatBuddy) {
        this.chatBuddy = chatBuddy;
        chat = new Chat();
    }

    public Chat getConversation() {
        return chat;
    }

    public String getChatBuddy() {
        return chatBuddy;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }
}