/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

package com.fagi.controller;

import com.fagi.action.items.OpenConversationFromID;
import com.fagi.controller.contentList.ContentController;
import com.fagi.controller.contentList.MessageItemController;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.controller.utility.Draggable;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationFilter;
import com.fagi.handler.Search;
import com.fagi.model.FriendMapWrapper;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.Logout;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.handlers.GeneralHandler;
import com.fagi.network.handlers.GeneralHandlerFactory;
import com.fagi.network.handlers.TextMessageHandler;
import com.fagi.utility.JsonFileOperations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * TODO: Write description.
 */
public class MainScreen {
    @FXML private Pane body;
    @FXML private Pane messages;
    @FXML private Pane contacts;
    @FXML private ScrollPane listContent;
    @FXML private Pane searchHeader;
    @FXML private TextField searchBox;

    public enum PaneContent {
        Contacts, Messages
    }

    private List<MessageItemController> messageItemControllers = new ArrayList<>();
    private ContentController conversationContentController;
    private FriendRequestList friendRequestList;
    private FriendMapWrapper friendMapWrapper;
    private ContentController contactContentController;
    private Search search;

    private Pane currentPane;
    private Map<PaneContent, Parent> listContentMap;
    private PaneContent currentPaneContent;

    private final String username;
    private final Communication communication;
    private List<com.fagi.conversation.Conversation> conversations;
    private Stage primaryStage;

    private Thread messageThread;
    private Thread voiceThread;
    private Draggable draggable;
    private GeneralHandler generalHandler;
    private Thread generalHandlerThread;
    private Conversation conversation = new Conversation();
    private FriendList friendList = new FriendList(new DefaultListAccess(new ArrayList<>()));
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
        listContentMap = new HashMap<>();
        this.primaryStage = primaryStage;
        this.friendMapWrapper = new FriendMapWrapper(this);
    }

    /**
     * Initiate all communication and handlers needed to contact the server.
     */
    public void initCommunication() {
        conversations = JsonFileOperations.loadAllClientConversations(username);
        setupConversationList();
        setupFriendList();
        setupContactList();
        TextMessageHandler messageHandler = new TextMessageHandler(this);
        messageThread = new Thread(messageHandler.getRunnable());
        messageThread.start();

        GeneralHandlerFactory factory = new GeneralHandlerFactory(this);
        generalHandler = factory.construct();
        generalHandlerThread = new Thread(generalHandler.getRunnable());
        generalHandlerThread.start();

        updateConversationListFromServer(conversations);
    }

    @FXML
    void initialize() {
        currentPane = messages;
        currentPaneContent = PaneContent.Messages;
        changeMenuStyle(PaneContent.Contacts.toString());
        search = new Search(searchBox, searchHeader, this);
    }

    @FXML
    void stopSearching() {
        search.stopSearching();
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

        for (MessageItemController controller : this.messageItemControllers) {
            controller.stopTimer();
        }
    }

    private void interrupt(Thread thread) {
        thread.interrupt();
        while (!thread.isInterrupted()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        draggable.mousePressed(mouseEvent);
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        draggable.mouseDragged(mouseEvent);
    }

    public void setScrollPaneContent(PaneContent content, Parent parent) {
        if (currentPaneContent == content) {
            listContent.setContent(parent);
        }

        listContentMap.put(content, parent);
    }

    public void setFriendList(FriendList friendList) {
        this.friendList = friendList;
    }

    public void setFriendRequestList(FriendRequestList friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    @FXML
    public void changeMenu(MouseEvent event) {
        Node node = (Node) event.getSource();
        changeMenuStyle((String) node.getUserData());
        search.stopSearching();
        body.requestFocus();
    }

    public void changeMenuStyle(String menu) {
        currentPane.getStyleClass().removeAll("chosen");
        currentPane.getStyleClass().add("button-shape");

        switch (menu) {
            case "Contacts":
                currentPaneContent = PaneContent.Contacts;
                currentPane = contacts;
                listContent.setContent(listContentMap.get(PaneContent.Contacts));
                break;
            case "Messages":
                currentPaneContent = PaneContent.Messages;
                currentPane = messages;
                listContent.setContent(listContentMap.get(PaneContent.Messages));
                break;
            default:
                System.err.println("Mainscreen, changeMenuStyle: " + menu);
                throw new NotImplementedException();
        }
        currentPane.getStyleClass().removeAll("button-shape");
        currentPane.getStyleClass().add("chosen");
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public void addConversation(Conversation conversation) {
        conversations.add(conversation);
        Pane pane = createMessageItem(conversation);
        Platform.runLater(() -> conversationContentController.addToContentList(pane));
    }

    public List<MessageItemController> getMessageItemControllers() {
        return messageItemControllers;
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

    public PaneContent getCurrentPaneContent() {
        return currentPaneContent;
    }

    public Parent getListContent(PaneContent content) {
        return listContentMap.get(content);
    }

    private void updateConversationListFromServer(List<Conversation> conversations) {
        List<ConversationFilter> filters = conversations.stream().map(
            x -> new ConversationFilter(x.getId(), x.getLastMessageDate()))
                                                        .collect(Collectors.toList());

        communication.sendObject(new GetConversationsRequest(username, filters));
    }

    private void setupFriendList() {
        communication.sendObject(new GetFriendListRequest(username));
    }

    private void setupContactList() {
        ContentController contactContentController = new ContentController();
        FXMLLoader contentLoader =
            new FXMLLoader(getClass().getResource("/com/fagi/view/content/ContentList.fxml"));
        contentLoader.setController(contactContentController);
        try {
            VBox contactContent = contentLoader.load();
            setScrollPaneContent(PaneContent.Contacts, contactContent);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setupConversationList() {
        conversationContentController = new ContentController();
        FXMLLoader contentLoader =
            new FXMLLoader(getClass().getResource("/com/fagi/view/content/ContentList.fxml"));
        contentLoader.setController(conversationContentController);
        try {
            VBox messagesContent = contentLoader.load();
            setScrollPaneContent(PaneContent.Messages, messagesContent);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        for (Conversation conversation : conversations) {
            conversationContentController.addToContentList(createMessageItem(conversation));
        }
    }

    public Pane createMessageItem(Conversation conversation) {
        MessageItemController messageItemController =
            new MessageItemController(username, conversation.getId());
        messageItemControllers.add(messageItemController);
        messageItemController.assign(new OpenConversationFromID(this, conversation.getId()));

        FXMLLoader loader =
            new FXMLLoader(getClass().getResource("/com/fagi/view/content/Conversation.fxml"));
        loader.setController(messageItemController);
        Pane pane = null;
        try {
            pane = loader.load();

            messageItemController.setUsers(conversation.getParticipants());

            if (conversation.getLastMessage() != null) {
                TextMessage lastMessage = conversation.getLastMessage();
                messageItemController.setLastMessage(lastMessage.getData(),
                                                     lastMessage.getMessageInfo().getSender());
                messageItemController.setDate(conversation.getLastMessageDate());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return pane;
    }

    public ContentController getConversationContentController() {
        return conversationContentController;
    }

    public void addElement(Node node) {
        body.getChildren().add(node);
    }

    public void removeElement(Node node) {
        body.getChildren().remove(node);
    }

    public void setConversationController(ConversationController conversationController) {
        this.conversationController = conversationController;
    }

    public void setConversationContentController(ContentController conversationContentController) {
        this.conversationContentController = conversationContentController;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public FriendMapWrapper getFriendMapWrapper() {
        return friendMapWrapper;
    }

    public ContentController getContactContentController() {
        return contactContentController;
    }

    public void setContactContentController(ContentController contactContentController) {
        this.contactContentController = contactContentController;
    }

    public FriendList getFriendList() {
        return friendList;
    }
}
