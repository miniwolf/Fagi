/*
 * Copyright (c) 2015. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package model;

/**
 * @author miniwolf
 */
public class TextMessage extends Message<String> {
    /**
     * Containing the message text
     */
    private final String text;

    public TextMessage(String text, String sender, String receiver) {
        super(sender, receiver);
        this.text = text;
    }

    public String getData() {
        return text;
    }
}
