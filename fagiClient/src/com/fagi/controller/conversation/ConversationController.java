/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.conversation;

import com.fagi.action.items.LoadHTML;
import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.Communication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class ConversationController extends BorderPane {
    @FXML private Label name;
    @FXML private Label date;
    @FXML private TextArea conversationTextarea;
    @FXML private VBox chat;
    @FXML private ScrollPane scroller;

    private Conversation conversation;
    private MainScreen mainScreen;
    private Communication communication;
    private String username;
    private final Stage primaryStage;
    private boolean isUserScrolling = false;
    private boolean isInternalScroll = false;

    public ConversationController(MainScreen mainScreen, Conversation conversation,
                                  String username) {
        this.conversation = conversation;
        this.mainScreen = mainScreen;
        this.username = username;
        this.communication = mainScreen.getCommunication();
        this.primaryStage = mainScreen.getPrimaryStage();
        new LoadHTML(this, engine, "/com/fagi/view/conversation/Conversation.fxml").execute();
    }

    @FXML
    private void initialize() {
        String titleNames = conversation.getParticipants().stream()
                                        .filter(s -> !s.equals(username))
                                        .collect(Collectors.joining(", "));
        name.setText(titleNames);

        String dateString = dateToString(conversation.getLastMessageDate());
        if ("".equals(dateString)) {
            date.setMinHeight(Region.USE_PREF_SIZE);
        }
        date.setText(dateString);

        conversationTextarea.setOnKeyPressed(this::handleEnterBehaviour);

        fillChat();

        scroller.vvalueProperty().addListener(
                observable -> this.isUserScrolling = scroller.getVvalue() != 1.0
                                                     && !isInternalScroll);
    }

    private void handleEnterBehaviour(KeyEvent event) {
        if (event.getCode() != KeyCode.ENTER) {
            return;
        }

        if (!event.isShiftDown()) {
            sendMessage();
            conversationTextarea.setText("");
        } else {
            conversationTextarea.setText(conversationTextarea.getText() + "\n");
            conversationTextarea.positionCaret(conversationTextarea.getText().length());
        }
        event.consume();
    }

    private void sendMessage() {
        TextMessage textMessage = new TextMessage(
                conversationTextarea.getText(),
                username,
                conversation.getId());
        communication.sendObject(textMessage);
    }

    private void fillChat() {
        Platform.runLater(() -> {
            for (TextMessage message : conversation.getMessages()) {
                addMessage(message);
            }
        });
    }

    private String dateToString(Date date) {
        if (date == null) {
            return "";
        } else {
            // Convert into something like "active 3 mo ago" "active 1 w ago"
            return "";
        }
    }

    /**
     * Adds the UI for the message and updates the content list with the message data.
     *
     * @param message TextMessage representation of the message received.
     */
    public void addMessage(TextMessage message) {
        Platform.runLater(() -> {
            if (!isUserScrolling) {
                isInternalScroll = true;
            }

            chat.getChildren().add(createMessageBox(message));
            primaryStage.sizeToScene();

            if (!isUserScrolling) {
                scroller.setVvalue(1.0);
            }

            isInternalScroll = false;
        });
    }

    public void redrawMessages() {
        chat.getChildren().clear();
        fillChat();
    }

    private HBox getBox(String message) {
        return new MessageController(message, "/com/fagi/view/conversation/MyMessage.fxml");
    }

    private HBox getBox(String message, String username) {
        return new MessageController(message, "/com/fagi/view/conversation/TheirMessage.fxml",
                                     username);
    }

    private HBox createMessageBox(TextMessage message) {
        return message.getMessageInfo().getSender().equals(username)
               ? getBox(message.getData())
               : getBox(message.getData(), message.getMessageInfo().getSender());
    }

    @FXML
    public void closeConversation() {
        mainScreen.removeElement(this);
        mainScreen.removeConversation(conversation);
        mainScreen.removeConversationController(this);
    }

    public Conversation getConversation() {
        return conversation;
    }
}
