package com.fagi.controller;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

import com.fagi.exceptions.UserOnlineException;
import com.fagi.model.Chat;
import com.fagi.model.Conversation;
import com.fagi.model.Logout;
import com.fagi.model.TextMessage;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.MessageListener;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO: Write description
 */
public class MainScreen {
    @FXML
    private ListView<String> contactList;
    @FXML
    private ListView<String> requestList;
    @FXML
    private ScrollPane scrollPaneChat;
    @FXML
    private TextArea chatMessage;
    @FXML
    private TextArea message;
    @FXML
    private Label chatname;

    private final String username;
    private final Communication communication;
    private ArrayList<Conversation> conversations;
    private MessageListener messageListener;
    private Thread messageThread;
    private Stage primaryStage;

    /**
     * Creates new form ContactScreen
     *
     * @param username      which is used all around the class for knowing who the user is
     * @param communication granted by the LoginScreen class
     */
    public MainScreen(String username, Communication communication) {
        this.username = username;
        this.communication = communication;
    }

    public void initCommunication() {
        conversations = new ArrayList<>();
        messageListener = new MessageListener(communication, contactList, requestList, this);
        messageListener.update(conversations);
        messageThread = new Thread(messageListener);
        messageThread.start();
    }

    /**
     * Callback from FagiApp class.
     * Used to initialize the form.
     */
    public void initComponents() {
        message.setOnKeyPressed(event -> {
            if ( event.getSource() != message || event.getCode() != KeyCode.ENTER ) {
                return;
            }
            if ( event.isControlDown() ) {
                message.appendText("\n");
            } else {
                handleMessage();
                event.consume();
            }
        });

        scrollPaneChat.setContent(new Chat("nobody, that's sad."));

        //contactList.setCellFactory(param ->
        // new ListCellRenderer(messageListener, scrollPaneChat));

        //    jContactList.setSelectionBackground(new Color(255, 153, 51));
    }

    private void handleMessage() {
        if ( message.getText().equals("") ) {
            return;
        }

        Chat chat = (Chat) scrollPaneChat.getContent();
        for ( Conversation conversation : conversations ) {
            if ( conversation.getConversation() == chat ) {
                communication.sendObject(new TextMessage(message.getText(), username,
                                                         conversation.getChatBuddy()));
                Exception exception;
                while ( (exception = communication.getNextException()) == null ) {}
                if ( exception instanceof UserOnlineException ) {
                    conversation.getConversation().appendText("System: User went offline.");
                }
                break;
            }
        }

        chat.appendText(username + ": " + message.getText() + "\n");
        message.setText("");
        message.requestFocus();
    }

    @FXML
    void talkButtonClicked() {

    }

    @FXML
    void requestListClicked() {
        try {
            //URL f = new File("D:/Github/Fagi/fagiClient/src/com.fagi.view/RequestRespond.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/fagi/view/RequestRespond.fxml"));
            GridPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Request Respond");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            requestList.setOnMouseClicked(event -> {
                if ( event.getSource() != requestList
                     || !event.getButton().equals(MouseButton.PRIMARY) ) {
                    return;
                }

                String user = requestList.getFocusModel().getFocusedItem();
                if ( user == null || user.length() == 0 ) {
                    return;
                }

                RespondController controller = loader.getController();
                controller.setStage(dialogStage);
                controller.setRequestName(user);

                dialogStage.showAndWait();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void contactListClicked() {
        String chatBuddy = contactList.getFocusModel().getFocusedItem();
        if ( chatBuddy == null || "".equals(chatBuddy) ) return;

        boolean exists = false;
        for ( Conversation conversation : conversations ) {
            if ( conversation.getChatBuddy().equals(chatBuddy) ) {
                scrollPaneChat.setContent(conversation.getConversation());
                chatname.setText(chatBuddy);
                if ( messageListener.unread.indexOf(chatBuddy) != -1 ) {
                    messageListener.unread.remove(chatBuddy);
                    //contactList.repaint();
                }
                exists = true;
                break;
            }
        }

        if ( !exists ) {
            updateConversations(chatBuddy);
            return;
        }
        message.requestFocus();
    }

    public void updateConversations(String chatBuddy) {
        Conversation conversation = new Conversation(chatBuddy);
        conversations.add(conversation);
        messageListener.update(conversations);
    }

    @FXML
    void logoutRequest() {
        messageListener.close();
        messageThread.interrupt();
        /* Have to wait, else the listener will try asking
           request using the closed socket causing a SocketException. */
        while ( !messageThread.isInterrupted() ) { }
        ChatManager.handleLogout(new Logout());
    }

    /**
     * Opens a dialog to send a friend request to the server. When the user clicks
     * Send Request, the method will call ChatManager with the content of the request
     * TextField.
     */
    @FXML
    void menuFriendRequest() {
        try {
            // URL f = new File("D:/Github/Fagi/fagiClient/src/com.fagi.view/FriendRequest.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fagi/view/FriendRequest.fxml"));
            GridPane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Friend Request");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            RequestController controller = loader.getController();
            controller.setStage(dialogStage);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ScrollPane getConversationWindow() {
        return scrollPaneChat;
    }

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void sendButtonClicked() {
        handleMessage();
    }
}
