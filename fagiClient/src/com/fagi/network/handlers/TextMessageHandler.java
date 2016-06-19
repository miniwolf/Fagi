/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.model.Conversation;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.InputHandler;
import com.fagi.network.ListCellRenderer;

import javafx.application.Platform;
import java.util.ArrayList;
import java.util.List;

/**
 * @author miniwolf
 */
public class TextMessageHandler implements Handler {
    private Container container = new DefaultContainer();
    private Runnable runnable = new DefaultThreadHandler(container, this);

    private List<Conversation> conversations;
    private List<ListCellRenderer> listCellRenderer;
    private List<Object> unread = new ArrayList<>();
    private final MainScreen mainScreen;

    public TextMessageHandler(MainScreen mainScreen) {
        InputHandler.register(TextMessage.class, container);
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages inMessage) {
        TextMessage message = (TextMessage) inMessage;
        String chatBuddy = message.getMessage().getSender();
        for ( Conversation conversation : conversations ) {
            if ( !conversation.getChatBuddy().equals(chatBuddy) ) {
                continue;
            }

            Platform.runLater(() -> conversation.getConversation().appendText(
                    conversation.getChatBuddy() + ": " + message.getData() + "\n"));
            if ( mainScreen.getConversationWindow().getContent().equals(
                    conversation.getConversation()) ) {
                return;
            }

            unread.add(chatBuddy);
            listCellRenderer.stream().filter(cell -> chatBuddy.equals(cell.getText()))
                            .forEach(cell -> Platform.runLater(
                                    () -> cell.updateItem(chatBuddy, false)));
            return;
        }
        mainScreen.updateConversations(chatBuddy);
        handle(message);
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    /**
     * Updates the current conversation list.
     *
     * @param conversations new list of friend conversations.
     */
    public void update(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * Updating the renderer for the list. Switching between when switching tabs
     *
     * @param listCellRenderer ListCellRenderer for rendering new objects.
     */
    public void setListCellRenderer(List<ListCellRenderer> listCellRenderer) {
        this.listCellRenderer = listCellRenderer;
    }
}
