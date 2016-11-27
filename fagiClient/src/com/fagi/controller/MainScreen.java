package com.fagi.controller;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

import com.fagi.controller.conversation.ConversationController;
import com.fagi.controller.utility.Draggable;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationFilter;
import com.fagi.conversation.ConversationType;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.model.Logout;
import com.fagi.model.SearchUsersRequest;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.handlers.*;
import com.fagi.utility.JsonFileOperations;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: Write description.
 */
public class MainScreen {
	@FXML private Pane body;
	@FXML private ScrollPane listContent;
	@FXML private TextField searchBox;
	@FXML private Pane messages;
	@FXML private Pane contacts;

	private Pane currentPane;

	private final String username;
	private final Communication communication;
	private List<com.fagi.conversation.Conversation> conversations;
	private Stage primaryStage;

	private TextMessageHandler messageHandler;
	private Thread messageThread;
	private Thread voiceThread;
	private Draggable draggable;
	private GeneralHandler generalHandler;
	private Thread generalHandlerThread;
	private Conversation conversation = new Conversation();
	private FriendList friendList = new FriendList(new DefaultListAccess(new ArrayList<>()));
	private boolean currentConversation;
	private ConversationController conversationController;

	/**
	 * Creates new form ContactScreen.
	 *
	 * @param username      which is used all around the class for knowing who the user is
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
        conversations = JsonFileOperations.loadAllClientConversations(username);
        messageHandler = new TextMessageHandler(this);
        messageThread = new Thread(messageHandler.getRunnable());
        messageThread.start();

		GeneralHandlerFactory factory = new GeneralHandlerFactory(this);
		generalHandler = factory.construct();
		generalHandlerThread = new Thread(generalHandler.getRunnable());
		generalHandlerThread.start();

		searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
			searchUser(newValue);
		});

		updateConversationListFromServer(conversations);
	}

	@FXML
	public void initialize() {
		currentPane = messages;
		changeMenu("messages");
	}

	@FXML
	void talkButtonClicked() {

	}

	@FXML
	void logoutRequest() {
		interrupt(messageThread);
		//interrupt(voiceThread);
		generalHandler.stop();
		interrupt(generalHandlerThread);

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
		if ( searchString.isEmpty() ) {
			FriendListHandler handler = new FriendListHandler(this);
			handler.handle(friendList);
			return;
		}

		communication.sendObject(new SearchUsersRequest(username, searchString));
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

	public void setFriendList(FriendList friendList) {
		this.friendList = friendList;
	}

	public void changeMenu(String menu) {
		currentPane.getStyleClass().removeAll("chosen");
		currentPane.getStyleClass().add("button-shape");

		switch ( menu ) {
			case "Contacts":
				currentPane = contacts;
				break;
			case "Messages":
				currentPane = messages;
				break;
		}

		currentPane.getStyleClass().removeAll("button-shape");
		currentPane.getStyleClass().add("chosen");
	}

	public void setConversation(Conversation conversation) {
		if ( this.conversation == null || this.conversation.getParticipants().equals(conversation.getParticipants()) ) {
			return;
		}

		if (conversation.getType() == ConversationType.Placeholder) {
			conversation.setType(ConversationType.Real);
			this.communication.sendObject(new GetAllConversationDataRequest(username, conversation.getId()));
		}

		ConversationController controller = new ConversationController(conversation, communication, username);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fagi/view/conversation/Conversation.fxml"));
		loader.setController(controller);
		try {
			VBox conversationBox = loader.load();
			body.getChildren().add(conversationBox);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.conversationController = controller;
		this.conversation = conversation;
	}

	public void addConversation(Conversation conversation) {
		conversations.add(conversation);
	}

	public Communication getCommunication() {
		return communication;
	}

	public List<Conversation> getConversations() {
		return conversations;
	}

	public String getUsername() {
		return username;
	}

	public Conversation getCurrentConversation() {
		return conversation;
	}

	public ConversationController getConversationController() {
		return conversationController;
	}

	private void updateConversationListFromServer(List<Conversation> conversations) {
		List<ConversationFilter> filters = conversations.stream().map(x -> new ConversationFilter(x.getId(), x.getDateLastMessageDate())).collect(Collectors.toList());

		communication.sendObject(new GetConversationsRequest(username, filters));
	}
}
