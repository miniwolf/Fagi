package com.fagi.network.handlers;

import com.fagi.action.items.contentlist.CreateSearchList;
import com.fagi.controller.MainScreen;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.InGoingMessages;

/**
 * Created by Marcus on 08-07-2016.
 */
public class SearchHandler implements Handler {
    private final MainScreen mainScreen;

    public SearchHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages object) {
        SearchUsersResult result = (SearchUsersResult) object;
        new CreateSearchList(mainScreen, result.getData().getUsernames()).execute();
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
