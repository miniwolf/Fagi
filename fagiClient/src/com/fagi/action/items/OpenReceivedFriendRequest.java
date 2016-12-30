package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ReceivedInvitationController;
import com.fagi.model.FriendRequest;

/**
 * Created by costa on 11-12-2016.
 */
public class OpenReceivedFriendRequest implements Action {
    private final MainScreen mainScreen;
    private final FriendRequest request;

    public OpenReceivedFriendRequest(MainScreen mainScreen, FriendRequest request) {
        this.mainScreen = mainScreen;
        this.request = request;
    }

    @Override
    public void execute() {
        mainScreen.addElement(new ReceivedInvitationController(request, mainScreen));
    }
}
