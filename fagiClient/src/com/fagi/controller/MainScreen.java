/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * MainScreen.java
 *
 * UserInterface, containing chat window and contact list
 */

package com.fagi.controller;

import com.fagi.action.items.LoadFXML;
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
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.handlers.GeneralHandler;
import com.fagi.network.handlers.GeneralHandlerFactory;
import com.fagi.network.handlers.TextMessageHandler;
import com.fagi.utility.JsonFileOperations;
import com.fagi.utility.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO: Write description.
 */
public class MainScreen extends Pane {
    @FXML private Pane messages;
    @FXML private Pane contacts;
    @FXML private ScrollPane listContent;
    @FXML private Pane searchHeader;
    @FXML private TextField searchBox;
    @FXML private Pane dropdown;
    @FXML private Polygon dropdownExtra;
    @FXML private Label username;
    @FXML private Pane conversationHolder;
    @FXML private ImageView largeIcon;
    @FXML private ImageView tinyIcon;

    private Parent emptyFocusElement;
    private boolean signOut;

    public enum PaneContent {
        Contacts, Messages
    }

    private List<MessageItemController> messageItems = new ArrayList<>();
    private ContentController conversationContentController;
    private FriendRequestList friendRequestList;
    private FriendMapWrapper friendMapWrapper;
    private ContentController contactContentController;
    private Search search;

    private Pane currentPane;
    private Map<PaneContent, Parent> listContentMap;
    private PaneContent currentPaneContent;

    private final String usernameString;
    private final Communication communication;
    private List<Conversation> conversations;
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
     * @param usernameString which is used all around the class for knowing who the user is
     * @param communication  granted by the LoginScreen class
     * @param primaryStage   primary stage used to create a draggable.
     */
    public MainScreen(String usernameString, Communication communication, Stage primaryStage) {
        this.usernameString = usernameString;
        this.communication = communication;
        this.draggable = new Draggable(primaryStage);
        listContentMap = new HashMap<>();
        this.primaryStage = primaryStage;
        this.friendMapWrapper = new FriendMapWrapper(this);
        primaryStage.addEventHandler(MouseEvent.MOUSE_PRESSED,
                                     event -> System.out.println("mouse click detected: "
                                                                 + event.getTarget()));

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.setIconified(true);
        });

        primaryStage.setOnShowing(event -> System.out.println("Test"));

        new LoadFXML(this, "/com/fagi/view/Main.fxml").execute();
    }

    /**
     * Initiate all communication and handlers needed to contact the server.
     */
    public void initCommunication() {
        conversations = JsonFileOperations.loadAllClientConversations(usernameString);
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
    private void initialize() {
        currentPane = messages;
        currentPaneContent = PaneContent.Messages;
        changeMenuStyle(PaneContent.Messages.toString());
        emptyFocusElement = messages;
        username.setText(usernameString);
        Image tiny = new Image("/com/fagi/style/material-icons/" + usernameString.toCharArray()[0] + ".png", 40, 40, true, true);
        this.tinyIcon.setImage(tiny);
        Image large = new Image("/com/fagi/style/material-icons/" + usernameString.toCharArray()[0] + ".png", 96, 96, true, true);
        this.largeIcon.setImage(large);
        this.requestFocus();

        Scene scene = primaryStage.getScene();
        final MainScreen mainScreen = this;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Logger.logStackTrace(e);
                        } finally {
                            Platform.runLater(() -> {
                                System.out.println("Width: " + newSceneWidth);
                                search = new Search(searchBox, searchHeader, mainScreen);
                            });
                        }
                    }
                };

                Thread t = new Thread(run);
                t.start();

                scene.widthProperty().removeListener(this);
            }
        });
        /*
        searchBox.onMouseClickedProperty().addListener(event -> {
            System.out.println("Humus");
            if (search == null) {
                search = new Search(searchBox, searchHeader, this);
            }
        });
        */
        //instantiateSearchFunction();
    }

    @FXML
    private void stopSearching() {
        if (search != null) {
            search.stopSearching();
        }
    }

    @FXML
    private void talkButtonClicked() {

    }

    @FXML
    private void toggleSignOut() {
        signOut = !signOut;
        dropdown.setVisible(signOut);
        dropdownExtra.setVisible(signOut);
    }

    @FXML
    private void logoutRequest() {
        interrupt(messageThread);
        //interrupt(voiceThread);
        generalHandler.stop();
        interrupt(generalHandlerThread);

        ChatManager.handleLogout(new Logout());

        this.primaryStage.setOnCloseRequest(event -> {});

        for (MessageItemController controller : this.messageItems) {
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
                Logger.logStackTrace(ie);
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
    private void changeMenu(MouseEvent event) {
        Node node = (Node) event.getSource();
        changeMenuStyle((String) node.getUserData());
        search.stopSearching();
        requestFocus();
    }

    public void changeMenuStyle(String menu) {
        currentPane.getStyleClass().removeAll("chosen");
        currentPane.getStyleClass().add("button-shape");

        switch (menu) {
            case "Contacts":
                currentPaneContent = PaneContent.Contacts;
                currentPane = contacts;
                break;
            case "Messages":
                currentPaneContent = PaneContent.Messages;
                currentPane = messages;
                break;
            default:
                System.err.println("Mainscreen, changeMenuStyle: " + menu);
                throw new UnsupportedOperationException();
        }

        listContent.setContent(listContentMap.get(currentPaneContent));
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

    public List<MessageItemController> getMessageItems() {
        return messageItems;
    }

    public Communication getCommunication() {
        return communication;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public String getUsername() {
        return usernameString;
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

        communication.sendObject(new GetConversationsRequest(usernameString, filters));
    }

    private void setupFriendList() {
        communication.sendObject(new GetFriendListRequest(usernameString));
    }

    private void setupContactList() {
        ContentController contactContentController =
            new ContentController("/com/fagi/view/content/ContentList.fxml");
        setScrollPaneContent(PaneContent.Contacts, contactContentController);
    }

    private void setupConversationList() {
        conversationContentController =
            new ContentController("/com/fagi/view/content/ContentList.fxml");
        setScrollPaneContent(PaneContent.Messages, conversationContentController);

        for (Conversation conversation : conversations) {
            conversationContentController.addToContentList(createMessageItem(conversation));
        }
    }

    public Pane createMessageItem(Conversation conversation) {
        MessageItemController messageItemController =
            new MessageItemController(usernameString,
                                      conversation);
        messageItemController.getActionable()
                             .assign(new OpenConversationFromID(this, conversation.getId()));
        messageItems.add(messageItemController);
        return messageItemController;
    }

    public ContentController getConversationContentController() {
        return conversationContentController;
    }

    public void addElement(Node node) {
        conversationHolder.getChildren().add(node);
    }

    public void removeElement(Node node) {
        conversationHolder.getChildren().remove(node);
        emptyFocusElement.requestFocus();
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

    public void setCurrentPane(Pane currentPane) {
        this.currentPane = currentPane;
    }

    public void setListContent(VBox listContent) {
        this.listContent.setContent(listContent);
    }

    private void instantiateSearchFunction() {
        Runnable task = () -> {
            while (getScene() == null) {
                System.out.println(getScene());
            }
            windowLoaded();
        };
        new Thread(task).start();
    }

    public void windowLoaded() {
        search = new Search(searchBox, searchHeader, this);
    }
}
