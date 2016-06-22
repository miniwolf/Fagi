/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

package com.fagi.network;

import com.fagi.network.handlers.TextMessageHandler;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * TODO: Missing highlight from current chat.
 */
public class ListCellRenderer extends ListCell<String> {
    private TextMessageHandler textMessageHandler;

    public ListCellRenderer(TextMessageHandler textMessageHandler) {
        this.textMessageHandler = textMessageHandler;
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(item);

        if ( item == null || textMessageHandler.getUnread().indexOf(item) == -1 ) {
            setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        } else if ( item.length() != 0 ) {
            setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        }
        /*
        if ( null == item || item.length() == 0 ) {
            return;
        }

        setText(item);

//        for ( Conversation conversation : listener.conversations ) {
//            if ( scrollPaneChat.getContent().equals(conversation.getConversation())
//                 && item.equals(conversation.getChatBuddy()) ) {
//                setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY,
//                                                                Insets.EMPTY)));
//                return;
//            }
//        }
        boolean b = listener.unread.indexOf(item) != -1;
        setBackground(new Background(new BackgroundFill(b
                                                        ? Color.BLUE
                                                        : Color.WHITE, CornerRadii.EMPTY,
                                                        Insets.EMPTY)));*/
    }

    /*public Component getListCellRendererComponent(JList paramlist, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if ( value == null) return null;

        setText(value.toString());

        return this;
    }*/
}
