package com.fagi.controller;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

import com.fagi.model.Chat;
import com.fagi.model.Conversation;
import com.fagi.model.Logout;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.ListCellRenderer;
import com.fagi.network.handlers.ListMessageHandler;
import com.fagi.network.handlers.TextMessageHandler;
import com.fagi.responses.Response;
import com.fagi.responses.UserOnline;

import javafx.application.Platform;
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
import java.util.List;

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
    private List<Conversation> conversations;
    private Stage primaryStage;
    private List<ListCellRenderer> listCellRenderer = new ArrayList<>();

    private TextMessageHandler messageHandler;
    private Thread messageThread;

    /**
     * Creates new form ContactScreen.
     *
     * @param username      which is used all around the class for knowing who the user is
     * @param communication granted by the LoginScreen class
     */
    public MainScreen(String username, Communication communication) {
        this.username = username;
        this.communication = communication;
    }

    /**
     * Initiate all communication and handlers needed to contact the server.
     */
    public void initCommunication() {
        conversations = new ArrayList<>();
        messageHandler = new TextMessageHandler(this);
        messageHandler.update(conversations);
        messageThread = new Thread(messageHandler.getRunnable());
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

        scrollPaneChat.setContent(new Chat());
        contactList.setCellFactory(param -> {
            ListCellRenderer renderer = new ListCellRenderer();
            listCellRenderer.add(renderer);
            return renderer;
        });
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
                Response response = communication.getNextResponse();
                if ( response instanceof UserOnline ) {
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
        if ( chatBuddy == null || "".equals(chatBuddy) ) {
            return;
        }

        boolean exists = false;
        for ( Conversation conversation : conversations ) {
            if ( conversation.getChatBuddy().equals(chatBuddy) ) {
                scrollPaneChat.setContent(conversation.getConversation());
                chatname.setText("Chatroom with " + chatBuddy);
                if ( ListMessageHandler.unread.indexOf(chatBuddy) != -1 ) {
                    ListMessageHandler.unread.remove(chatBuddy);
                    listCellRenderer.stream().filter(cell -> chatBuddy.equals(cell.getText()))
                                    .forEach(cell -> Platform.runLater(
                                            () -> cell.updateItem(chatBuddy, false)));
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
        //messageListener.update(conversations);
        messageHandler.update(conversations);
    }

    @FXML
    void logoutRequest() {
        messageThread.interrupt();
        /* Have to wait, else the listener will
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
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/fagi/view/FriendRequest.fxml"));
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
