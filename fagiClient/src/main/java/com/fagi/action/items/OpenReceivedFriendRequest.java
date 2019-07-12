package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ReceivedInvitationController;
import com.fagi.model.FriendRequest;

/**
 * Created by costa on 11-12-2016.
 */
public class OpenReceivedFriendRequest implements Action<FriendRequest> {
    private final MainScreen mainScreen;

    public OpenReceivedFriendRequest(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void execute(FriendRequest request) {
        mainScreen.addElement(new ReceivedInvitationController(request, mainScreen));
    }
}
