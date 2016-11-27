package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
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
    }

    @Override
    public Runnable getRunnable() {
        return () -> {};
    }
}
