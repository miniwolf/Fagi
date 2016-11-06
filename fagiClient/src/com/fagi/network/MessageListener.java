package com.fagi.network;
/**
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * MessageListener.java
 */

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.messages.lists.ListAccess;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.List;

/**
 * For receiving messages and printing these to the conversation.
 * TODO: Add descriptions, add friend requests
 */
public class MessageListener {
    private final Communication communication;
    private List<Conversation> conversations;
    private final ListView<String> contactList;
    private final ListView<String> requestList;
    private List<ListCellRenderer> listCellRenderer;
    private final MainScreen mainScreen;
    private boolean running = true;

    public MessageListener(Communication communication, ListView<String> contactList,
                           ListView<String> requestList, MainScreen mainScreen) {
        this.communication = communication;
        this.contactList = contactList;
        this.requestList = requestList;
        this.mainScreen = mainScreen;
    }

    /**
     * Thread method, sending the text to the JTextArea
     * and updating our friend list.
     */
    /*public void run() {
        while ( running ) {
            MessageInfo message = null;
            while ( message == null && running ) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                FriendList friends = communication.getFriends();
                if ( friends != null ) {
                    update(contactList, friends);
                }
                FriendRequestList requests = communication.getRequests();
                if ( requests != null ) {
                    update(requestList, requests);
                }

                message = communication.receiveMessage();
            }
            if ( message instanceof TextMessage ) {
                updateConversation((TextMessage) message);
            } else if ( message instanceof VoiceMessage ) {
                new VoiceReceiver().playAudio((VoiceMessage) message);
            }
        }
    }*/

    /**
     * Updates the current conversation array.
     *
     * @param conversations new friend list to insert instead of previous one.
     */
    public void update(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    /**
     * Internal use, will update given JActionList to display
     * information given by the input.
     *
     * @param list ListView to output input into.
     * @param input List of users.
     */
    private void update(ListView<String> list, ListAccess input) {
        if ( input.getData().isEmpty() ) { // TODO: Cheaper to not set if already empty?
            Platform.runLater(() -> list.getItems().setAll(""));
            return;
        }

        ObservableList<String> observableList = list.getItems();

        int modelSize = observableList.size();
        if ( input.getData().size() != modelSize ) {
            Platform.runLater(() -> observableList.setAll(input.getData()));
            return;
        }

        for ( int i = 0; i < modelSize; i++ ) {
            if ( !input.getData().get(i).equals(observableList.get(i)) ) {
                Platform.runLater(() -> observableList.setAll(input.getData()));
                return;
            }
        }
    }
    public void close() {
        running = false;
    }

    public void setListCellRenderer(List<ListCellRenderer> listCellRenderer) {
        this.listCellRenderer = listCellRenderer;
    }
}

