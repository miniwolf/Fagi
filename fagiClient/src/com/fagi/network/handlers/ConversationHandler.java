package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.messages.InGoingMessages;

/**
 * @author miniwolf
 */
public class ConversationHandler implements Handler {
    private final MainScreen mainScreen;

    /**
     * Constructor.
     * @param mainScreen handle to the mainscreen. Used for adding the conversation.
     */
    public ConversationHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages object) {
        Conversation conversation = (Conversation) object;
        mainScreen.addConversation(conversation);
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
