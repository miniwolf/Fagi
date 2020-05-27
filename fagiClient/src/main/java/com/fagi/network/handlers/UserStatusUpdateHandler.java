package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.model.UserStatusUpdate;

/**
 * Created by costa on 13-12-2016.
 */
public class UserStatusUpdateHandler implements Handler<UserStatusUpdate> {
    private final MainScreen mainScreen;

    public UserStatusUpdateHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(UserStatusUpdate update) {
        mainScreen
                .getFriendMapWrapper()
                .toggleUserStatus(update.getUsername());
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
