/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.model;

import java.sql.Timestamp;

/**
 * @author miniwolf
 */
public class TextMessage extends Message<String> {
    /**
     * Containing the message text
     */
    private final String text;
    private Timestamp timeStamp;

    public TextMessage(String text, String sender, long conversationID) {
        super(sender, conversationID);
        this.text = text;
    }

    public String getData() {
        return text;
    }

    public void setTimeStamp(Timestamp timeStamp) { this.timeStamp = timeStamp; }
    public Timestamp getTimeStamp() { return timeStamp; }
}
