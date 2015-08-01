package com.fagi.network;/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * MessageListener.java
 *
 * For receiving messages and printing these to the conversation
 */

import com.fagi.controller.MainScreen;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import com.fagi.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Add descriptions, add friend requests
 */
public class MessageListener implements Runnable {
    private final Communication communication;
    public ArrayList<Conversation> conversations;
    private final ListView<String> contactList;
    private final ListView<String> requestList;
    private final MainScreen mainScreen;
    public final ArrayList<Object> unread = new ArrayList<>();
    private boolean running = true;

    public MessageListener(Communication communication, ListView<String> contactList, ListView<String> requestList, MainScreen mainScreen) {
        this.communication = communication;
        this.contactList = contactList;
        this.requestList = requestList;
        this.mainScreen = mainScreen;
    }

    /**
     * Updates the current conversation array.
     *
     * @param conversations new friend list to insert instead of previous one.
     */
    public void update(ArrayList<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * Thread method, sending the text to the JTextArea
     * and updating our friend list.
     */
    public void run() {
        while ( running ) {
            Message message = null;
            while ( message == null && running ) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                List<String> friends = communication.getList(new GetFriends());
                update(contactList, friends);
                List<String> requests = communication.getList(new GetRequests());
                update(requestList, requests);

                message = communication.receiveMessage();
            }
            if ( message instanceof TextMessage ) {
                updateConversation((TextMessage) message);
            } else if ( message instanceof VoiceMessage ) {
                new VoiceReceiver().playAudio((VoiceMessage) message);
            }
        }
    }

    /**
     * Internal use, will update given JActionList to display
     * information given by the input.
     *
     * @param list ListView to output input into.
     * @param input List of users.
     */
    private void update(ListView<String> list, List<String> input) {
        if ( input.isEmpty() ) { // TODO: Cheaper to not set if already empty?
            Platform.runLater(() -> list.getItems().setAll(""));
            return;
        }

        ObservableList<String> observableList = list.getItems();

        int modelSize = observableList.size();
        if ( input.size() != modelSize ) {
            Platform.runLater(() -> observableList.setAll(input));
            return;
        }

        for ( int i = 0; i < modelSize; i++ )
            if ( !input.get(i).equals(observableList.get(i)) ) {
                Platform.runLater(() -> observableList.setAll(input));
                return;
            }
    }

    private void updateConversation(TextMessage message) {
        String chatBuddy = message.getSender();
        for ( Conversation conversation : conversations ) {
            if ( !conversation.getChatBuddy().equals(chatBuddy) ) continue;

            Platform.runLater(() -> conversation.getConversation().appendText(conversation.getChatBuddy() + ": " + message.getData() + "\n"));
            if ( mainScreen.getConversationWindow().getContent().equals(conversation.getConversation()) )
                return;

            unread.add(chatBuddy);
            //contactList.repaint(); Give the cell renderer an opportunity to remove highlight
            return;
        }
        mainScreen.updateConversations(chatBuddy);
        updateConversation(message);
    }

    public void close() {
        running = false;
    }
}

