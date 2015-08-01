/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package com.fagi.network;

import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import com.fagi.model.Conversation;

/**
 * TODO: Missing highlight from current chat.
 */
public class ListCellRenderer extends ListCell<String> {
    private final MessageListener listener;
    private final ScrollPane scrollPaneChat;

    public ListCellRenderer(MessageListener listener, ScrollPane scrollPaneChat) {
        this.listener = listener;
        this.scrollPaneChat = scrollPaneChat;
    }

    public void updateItem(String item, boolean empty) {
        if ( null == item ) return;

        super.updateItem(item, empty);
        setText(item);

        for ( Conversation conversation : listener.conversations )
            if ( scrollPaneChat.getContent().equals(conversation.getConversation()) &&
                    item.equals(conversation.getChatBuddy()) ) {
                setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                return;
            }

        setBackground(new Background(new BackgroundFill(listener.unread.indexOf(item) != -1 ? Color.BLUE : Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /*public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if ( value == null) return null;

        setText(value.toString());

        return this;
    }*/
}
