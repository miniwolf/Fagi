/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Chat.java
 *
 * Tool class for holding text for conversations.
 */

import javax.swing.JTextArea;
import java.awt.*;

class Chat extends JTextArea {
    private final String chatBuddy;

    public Chat(String chatBuddy) {
        this.chatBuddy = chatBuddy;
        initComponents();
    }

    private void initComponents() {
        super.setColumns(20);
        super.setEditable(false);
        super.setLineWrap(true);
        super.setRows(5);
        super.setMinimumSize(new Dimension(1, 20));
        super.setPreferredSize(new Dimension(59, 20));
        super.setText("You are now chatting with: " + chatBuddy + "\n");
    }

    public String getChatBuddy() {
        return chatBuddy;
    }
}