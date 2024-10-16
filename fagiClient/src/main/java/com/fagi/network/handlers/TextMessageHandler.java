/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.MessageItemController;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.InputDistributor;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;
import com.fagi.utility.JsonFileOperations;
import javafx.application.Platform;

import java.util.Optional;

/**
 * @author miniwolf
 */
public class TextMessageHandler implements Handler<TextMessage> {
    private Container<TextMessage> container = new DefaultContainer<>();
    private DefaultThreadHandler<TextMessage> runnable = new DefaultThreadHandler<>(container, this);
    private final MainScreen mainScreen;

    public TextMessageHandler(
            MainScreen mainScreen,
            InputDistributor inputDistributor) {
        container.setThread(runnable);
        inputDistributor.register(TextMessage.class, container);
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(TextMessage message) {
        Optional<Conversation> first = mainScreen
                .getConversations()
                .stream()
                .filter(c -> c.getId() == message
                        .getMessageInfo()
                        .getConversationID())
                .findFirst();
        if (first.isEmpty()) {
            System.err.println("Server sent a message before it sent the conversation of ID '" + message
                    .getMessageInfo()
                    .getConversationID() + "'to the profile.");
            return;
        }
        Conversation conversation = first.get();

        if (conversation.getType() == ConversationType.Placeholder) {
            ConversationType type = conversation
                    .getParticipants()
                    .size() > 2 ? ConversationType.Multi : ConversationType.Single;

            conversation.setType(type);
            mainScreen
                    .getCommunication()
                    .sendObject(new GetAllConversationDataRequest(mainScreen.getUsername(), conversation.getId()));
        }

        if (mainScreen.hasCurrentOpenConversation(conversation)) {
            ConversationController controller = mainScreen.getControllerFromConversation(conversation);
            controller.addMessage(message);

        }
        conversation
                .data()
                .addMessage(message);
        JsonFileOperations.storeClientConversation(conversation, mainScreen.getUsername());

        MessageItemController messageItemController = mainScreen
                .getMessageItems()
                .stream()
                .filter(x -> x.getID() == conversation.getId())
                .findFirst()
                .get();
        Platform.runLater(() -> {
            messageItemController.setDate(conversation.getLastMessageDate());
            messageItemController.setLastMessage(message);
        });
        mainScreen.applyCss();
        mainScreen.layout();
    }

    @Override
    public DefaultThreadHandler<TextMessage> getRunnable() {
        return runnable;
    }
}
