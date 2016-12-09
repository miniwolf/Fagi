/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.Communication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author miniwolf
 */
public class ConversationController {
	@FXML private Label name;
	@FXML private Label date;
	@FXML private TextArea message;
	@FXML private VBox chat;
	@FXML private ScrollPane scroller;

    private final Stage primaryStage;
    private Conversation conversation;
	private Communication communication;
	private String username;
	private boolean isUserScrolling = false;
	private boolean isInternalScroll = false;

	public ConversationController(Stage primaryStage, Conversation conversation, Communication communication, String username) {
        this.primaryStage = primaryStage;
        this.conversation = conversation;
		this.communication = communication;
		this.username = username;
	}

	@FXML
	public void initialize() {
		String titleNames = conversation.getParticipants().stream().collect(Collectors.joining(", "));
		name.setText(titleNames);

		String dateString = dateToString(conversation.getLastMessageDate());
		if ( "".equals(dateString) ) {
			date.setMinHeight(Region.USE_PREF_SIZE);
		}
		date.setText(dateString);

		message.setOnKeyPressed(event -> {
			if ( !event.isShiftDown() && event.getCode() == KeyCode.ENTER ) {
				sendMessage();
				message.setText("");
				event.consume();
			}
		});

		fillChat();

		scroller.vvalueProperty().addListener(observable -> {
			this.isUserScrolling = scroller.getVvalue() != 1.0 && !isInternalScroll;
			System.out.println(this.isUserScrolling);
		});
	}

	private void sendMessage() {
		TextMessage textMessage = new TextMessage(message.getText(), username, conversation.getId());
		communication.sendObject(textMessage);
	}

	private void fillChat() {
		Platform.runLater(() -> {
			for(TextMessage message : conversation.getMessages()) {
				chat.getChildren().add(createMessageBox(message));
			}
			primaryStage.sizeToScene();
		});
	}

	private String dateToString(Date s) {
		if ( s == null ) {
			return "";
		} else {
			// Convert into something like "active 3 mo ago" "active 1 w ago"
			return "active 3 mo ago";
		}
	}

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

	private HBox getBox(String resource, String message) {
		HBox load = null;
		try {
			MessageController messageController = new MessageController(message);
			FXMLLoader loader = new FXMLLoader(ConversationController.class.getClass().getResource(resource));
			loader.setController(messageController);
			load = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return load;
	}

	private HBox createMessageBox(TextMessage message) {
		return getBox(message.getMessageInfo().getSender().equals(username)
				? "/com/fagi/view/conversation/MyMessage.fxml"
				: "/com/fagi/view/conversation/TheirMessage.fxml", message.getData());
	}

    @FXML
    public void closeConversation() {

    }
}
