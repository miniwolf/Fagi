package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.UserLoggedIn;
import com.fagi.model.UserLoggedOut;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.network.InputDistributor;
import com.fagi.threads.ThreadPool;

/**
 * Created by Marcus on 08-07-2016.
 */
public class GeneralHandlerFactory {
    public GeneralHandler construct(MainScreen mainScreen, InputDistributor inputDistributor, ThreadPool threadPool) {
        GeneralHandler handler = new GeneralHandler(inputDistributor, threadPool);

        handler.registerHandler(SearchUsersResult.class, new SearchHandler(mainScreen));
        handler.registerHandler(FriendList.class, new FriendListHandler(mainScreen));
        handler.registerHandler(Conversation.class, new ConversationHandler(mainScreen));
        handler.registerHandler(ConversationDataUpdate.class, new ConversationDataUpdateHandler(mainScreen));
        handler.registerHandler(FriendRequestList.class, new FriendRequestHandler(mainScreen));
        handler.registerHandler(UserLoggedIn.class, new UserStatusUpdateHandler(mainScreen));
        handler.registerHandler(UserLoggedOut.class, new UserStatusUpdateHandler(mainScreen));

        return handler;
    }
}
