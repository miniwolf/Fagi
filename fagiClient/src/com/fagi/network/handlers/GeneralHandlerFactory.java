package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;

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
        GeneralHandler.registerHandler(ConversationDataUpdate.class, new ConversationDataUpdateHandler(mainScreen));
        GeneralHandler.registerHandler(FriendRequestList.class, new FriendRequestHandler(mainScreen));

        return handler;
    }
}
