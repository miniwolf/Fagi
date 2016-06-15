/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fagi.controller.MainScreen;
import com.fagi.model.Conversation;
import com.fagi.model.TextMessage;

import com.fagi.network.Communication;
import com.fagi.network.ListCellRenderer;
import javafx.application.Platform;
import javafx.scene.control.ListView;


/**
 * @author miniwolf
 */
public class TextMessageHandler implements Handler<TextMessage> {
    private Queue<TextMessage> queue = new LinkedBlockingQueue<>();
    private List<Conversation> conversations;
    private final List<Object> unread = new ArrayList<>();
    private List<ListCellRenderer> listCellRenderer;
    private final MainScreen mainScreen;

    public TextMessageHandler(Communication communication, ListView<String> contactList,
                           ListView<String> requestList, MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void run() {
        while ( !queue.isEmpty() ) {
            updateConversation(queue.remove());
        }
    }

    @Override
    public void addObject(TextMessage textMessage) {
        queue.add(textMessage);
        this.notify();
    }

    private void updateConversation(TextMessage message) {
        String chatBuddy = message.getSender();
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
        updateConversation(message);
    }

    /**
     * Updates the current conversation list.
     *
     * @param conversations new list of friend conversations.
     */
    public void update(List<Conversation> conversations) {
        this.conversations = conversations;
    }
}
