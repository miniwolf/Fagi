/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Conversation.java
 *
 * Tool class contains information on name and textarea for a chat.
 */

/**
 *  TODO: Write description
 */
class Conversation extends java.awt.Component {
    private final javax.swing.JTextArea chat;
    private final String chatBuddy;

    public Conversation(String chatBuddy) {
        this.chatBuddy = chatBuddy;
        chat = new Chat(chatBuddy);
    }

    public javax.swing.JTextArea getConversation() {
        return chat;
    }

    public String getChatBuddy() {
        return chatBuddy;
    }
}