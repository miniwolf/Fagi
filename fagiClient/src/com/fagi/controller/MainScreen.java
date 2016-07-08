package com.fagi.controller;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

import com.fagi.controller.utility.Draggable;
import com.fagi.model.Conversation;
import com.fagi.model.Logout;
import com.fagi.model.SearchUsersRequest;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.ListCellRenderer;
import com.fagi.network.handlers.SearchHandler;
import com.fagi.network.handlers.TextMessageHandler;
import com.fagi.network.handlers.VoiceMessageHandler;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Write description.
 */
public class MainScreen {
    private String searchedString = "";

    @FXML private Pane body;
    @FXML private ScrollPane listContent;
    @FXML private TextField searchBox;

    private double xOffset;
    private double yOffset;

    private final String username;
    private final Communication communication;
    private List<Conversation> conversations;
    private Stage primaryStage;
    private List<ListCellRenderer> listCellRenderer = new ArrayList<>();

    private TextMessageHandler messageHandler;
    private Thread messageThread;
    private Thread voiceThread;
    private Thread listThread;
    private Draggable draggable;
    private SearchHandler searchHandler;
    private Thread searchThread;

    /**
     * Creates new form ContactScreen.
     *  @param username      which is used all around the class for knowing who the user is
     * @param communication granted by the LoginScreen class
     * @param primaryStage  primary stage used to create a draggable.
     */
    public MainScreen(String username, Communication communication, Stage primaryStage) {
        this.username = username;
        this.communication = communication;
        this.draggable = new Draggable(primaryStage);
    }

    /**
     * Initiate all communication and handlers needed to contact the server.
     */
    public void initCommunication() {
        conversations = new ArrayList<>();
        messageHandler = new TextMessageHandler(this);
        messageHandler.update(conversations);
        messageHandler.setListCellRenderer(listCellRenderer);
        messageThread = new Thread(messageHandler.getRunnable());
        messageThread.start();

        VoiceMessageHandler voiceHandler = new VoiceMessageHandler();
        voiceThread = new Thread(voiceHandler.getRunnable());
        voiceThread.start();

        searchHandler = new SearchHandler(this);
        searchThread = new Thread(searchHandler.getRunnable());
        searchThread.start();
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            searchUser(newValue);
        });
    }

    /**
     * Callback from FagiApp class.
     * Used to initialize the form.
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
        buildContactList();
    }

    private void buildContactList(List<Contact> list) {
        ObservableList<Node> children = contactList.getChildren();
        children.addAll(list.stream().map(Contact::build).collect(Collectors.toList()));
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
    }*/

    @FXML
    void talkButtonClicked() {

    }

    /*@FXML
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
                if ( messageHandler.getUnread().indexOf(chatBuddy) != -1 ) {
                    messageHandler.getUnread().remove(chatBuddy);
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
            contactListClicked();
            return;
        }
        message.requestFocus();
    }*/

    public void updateConversations(String chatBuddy) {
        Conversation conversation = new Conversation(chatBuddy);
        conversations.add(conversation);
        messageHandler.update(conversations);
    }

    @FXML
    void logoutRequest() {
        interrupt(messageThread);
//        interrupt(listThread);
        interrupt(voiceThread);
        interrupt(searchThread);

        ChatManager.handleLogout(new Logout());
    }

    private void interrupt(Thread thread) {
        thread.interrupt();
        while ( !thread.isInterrupted() ) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a dialog to send a friend request to the server. When the user clicks
     * Send Request, the method will call ChatManager with the content of the request
     * TextField.
     */
    @FXML
    public void showFriendRequestPopup() {
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

    @FXML
    public void searchUser(String searchString) {
        if (searchString.isEmpty()) {
            // TODO : Show friend list
            return;
        }

        communication.sendObject(new SearchUsersRequest(searchString));
    }

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        draggable.mousePressed(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        draggable.mouseDragged(mouseEvent);
    }

    public void setScrollPaneContent(Parent parent) {
        listContent.setContent(parent);
    }
}
