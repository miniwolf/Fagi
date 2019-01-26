package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.UserLoggedIn;
import com.fagi.model.UserLoggedOut;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;

/**
 * Created by Marcus on 08-07-2016.
 */
public class GeneralHandlerFactory {
    public GeneralHandler construct(MainScreen mainScreen) {
        GeneralHandler handler = new GeneralHandler();

        GeneralHandler.registerHandler(SearchUsersResult.class, new SearchHandler(mainScreen));
        GeneralHandler.registerHandler(FriendList.class, new FriendListHandler(mainScreen));
        GeneralHandler.registerHandler(Conversation.class, new ConversationHandler(mainScreen));
        GeneralHandler.registerHandler(ConversationDataUpdate.class, new ConversationDataUpdateHandler(mainScreen));
        GeneralHandler.registerHandler(FriendRequestList.class, new FriendRequestHandler(mainScreen));
        GeneralHandler.registerHandler(UserLoggedIn.class, new UserStatusUpdateHandler(mainScreen));
        GeneralHandler.registerHandler(UserLoggedOut.class, new UserStatusUpdateHandler(mainScreen));

        return handler;
    }
}
