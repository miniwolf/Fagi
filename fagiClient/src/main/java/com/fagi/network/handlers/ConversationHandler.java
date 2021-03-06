package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.utility.JsonFileOperations;

/**
 * @author miniwolf
 */
public record ConversationHandler(MainScreen mainScreen) implements Handler<Conversation> {
    /**
     * Constructor.
     *
     * @param mainScreen handle to the mainscreen. Used for adding the conversation.
     */
    public ConversationHandler {
    }

    @Override
    public void handle(Conversation conversation) {
        if (conversation.getType() != ConversationType.Placeholder) {
            JsonFileOperations.storeConversation(conversation);
        }
        mainScreen.addConversation(conversation);
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
