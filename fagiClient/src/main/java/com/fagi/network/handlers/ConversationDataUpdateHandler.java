package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.MessageItemController;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.utility.JsonFileOperations;
import javafx.application.Platform;

import java.util.Optional;

/**
 * Created by Marcus on 13-11-2016.
 */
public record ConversationDataUpdateHandler(MainScreen mainScreen) implements Handler<ConversationDataUpdate> {
    @Override
    public void handle(ConversationDataUpdate response) {
        Optional<Conversation> con = mainScreen
                .getConversations()
                .stream()
                .filter(x -> x.getId() == response.id())
                .findFirst();

        if (con.isEmpty()) {
            return;
        }
        Conversation conversation = con.get();

        if (conversation
                .getMessages()
                .isEmpty()) {
            response
                    .conversationData()
                    .forEach(conversation::addMessage);
        } else {
            conversation.addMessagesNoDate(response.conversationData());
        }

        JsonFileOperations.storeConversation(conversation);

        if (mainScreen.hasCurrentOpenConversation(conversation)) {
            Platform.runLater(() -> mainScreen
                    .getControllerFromConversation(conversation)
                    .redrawMessages());
        }

        MessageItemController messageItemController = mainScreen
                .getMessageItems()
                .stream()
                .filter(x -> x.getID() == conversation.getId())
                .findFirst()
                .get();
        Platform.runLater(() -> {
            if (conversation.getLastMessage() != null) {
                messageItemController.setLastMessage(response.lastMessage());
            }

            if (conversation.getLastMessageDate() != null) {
                messageItemController.setDate(conversation.getLastMessageDate());
            }
        });
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
