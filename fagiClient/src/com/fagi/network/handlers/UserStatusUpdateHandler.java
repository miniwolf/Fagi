package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.model.UserStatusUpdate;
import com.fagi.model.messages.InGoingMessages;

/**
 * Created by costa on 13-12-2016.
 */
public class UserStatusUpdateHandler implements Handler {
    private final MainScreen mainScreen;

    public UserStatusUpdateHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages object) {
        UserStatusUpdate update = (UserStatusUpdate)object;
        mainScreen.getFriendMapWrapper().toggleUserStatus(update.getUsername());
    }

    @Override
    public Runnable getRunnable() {
        return () -> {};
    }
}
