package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;

import java.util.Optional;

/**
 * @author miniwolf
 */
public class OpenConversationFromID implements Action {
    private final MainScreen mainScreen;
    private final long id;

    public OpenConversationFromID(MainScreen mainScreen, long id) {
        this.mainScreen = mainScreen;
        this.id = id;
    }

    @Override
    public void Execute() {
        Optional<Conversation> optConversation = mainScreen.getConversations().stream().filter(con -> con.getId() == id).findFirst();
        if ( !optConversation.isPresent() ) {
            return;
        }

        Conversation conversation = optConversation.get();
        mainScreen.setConversation(conversation);
    }
}
