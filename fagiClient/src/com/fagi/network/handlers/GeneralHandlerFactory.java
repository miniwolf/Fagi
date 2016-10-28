package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.lists.FriendList;

/**
 * Created by Marcus on 08-07-2016.
 */
public class GeneralHandlerFactory {

    private MainScreen mainScreen;

    public GeneralHandlerFactory(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public GeneralHandler construct() {
        GeneralHandler handler = new GeneralHandler();

        GeneralHandler.registerHandler(SearchUsersResult.class, new SearchHandler(mainScreen));
        GeneralHandler.registerHandler(FriendList.class, new FriendListHandler(mainScreen));
        GeneralHandler.registerHandler(Conversation.class, new ConversationHandler(mainScreen));

        return handler;
    }
}
