package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.MessageItemController;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.model.messages.InGoingMessages;
import javafx.application.Platform;

import java.util.Optional;

/**
 * Created by Marcus on 13-11-2016.
 */
public class ConversationDataUpdateHandler implements Handler {
    private final MainScreen mainScreen;

    public ConversationDataUpdateHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages object) {
        ConversationDataUpdate response = (ConversationDataUpdate) object;
        Optional<Conversation> con = mainScreen.getConversations().stream().filter(x -> x.getId() == response.getId()).findFirst();

        if (!con.isPresent()) return;
        Conversation conversation = con.get();

        if (conversation.getMessages().isEmpty()) {
            response.getConversationData().forEach(conversation::addMessage);
        } else {
            response.getConversationData().forEach(con.get()::addMessageNoDate);
        }

        if (mainScreen.getConversationController() != null && mainScreen.getCurrentConversation().getId() == response.getId()) {
            Platform.runLater(() -> mainScreen.getConversationController().redrawMessages());
        }

        MessageItemController messageItemController = mainScreen.getMessageItemControllers().stream().filter(x -> x.getID() == conversation.getId()).findFirst().get();
        Platform.runLater(() -> {
            if (conversation.getLastMessage() != null) {
                messageItemController.setLastMessage(response.getLastMessage().getData(), response.getLastMessage().getMessageInfo().getSender());
            }

            if (conversation.getLastMessageDate() != null) {
                messageItemController.setDate(conversation.getLastMessageDate());
            }
        });
    }

    @Override
    public Runnable getRunnable() {
        return () -> {};
    }
}
