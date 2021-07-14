package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.model.UserStatusUpdate;

/**
 * Created by costa on 13-12-2016.
 */
public record UserStatusUpdateHandler(MainScreen mainScreen) implements Handler<UserStatusUpdate> {
    @Override
    public void handle(UserStatusUpdate update) {
        mainScreen
                .getFriendMapWrapper()
                .toggleUserStatus(update.username());
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
