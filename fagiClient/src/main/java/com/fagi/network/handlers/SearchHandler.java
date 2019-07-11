package com.fagi.network.handlers;

import com.fagi.action.Action;
import com.fagi.action.items.contentlist.CreateSearchList;
import com.fagi.controller.MainScreen;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.InGoingMessages;

import java.util.List;

/**
 * Created by Marcus on 08-07-2016.
 */
public class SearchHandler implements Handler {
    private Action<List<String>> createSearchList;

    public SearchHandler(MainScreen mainScreen) {
        this.createSearchList = new CreateSearchList(mainScreen, false);
    }

    @Override
    public void handle(InGoingMessages object) {
        SearchUsersResult result = (SearchUsersResult) object;
        createSearchList.execute(result.getData().getUsernames());
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
