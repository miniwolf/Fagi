/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items.contentlist;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.SearchContactController;
import com.fagi.controller.contentList.ContentController;

import java.util.List;

import javafx.application.Platform;

/**
 * Creates the element and stores it in the mainscreen's current PaneContent element.
 *
 * @author miniwolf
 */
public class CreateSearchList implements Action {
    private MainScreen mainScreen;
    private List<String> usernames;
    private boolean isFriends;

    public CreateSearchList(MainScreen mainScreen, List<String> usernames, boolean isFriends) {
        this.mainScreen = mainScreen;
        this.usernames = usernames;
        this.isFriends = isFriends;
    }

    @Override
    public void execute() {
        ContentController contentController =
                new ContentController("/view/content/SearchContent.fxml");

        usernames.forEach(username -> {
            SearchContactController controller = new SearchContactController(isFriends, mainScreen,
                                                                             username);
            contentController.addToContentList(controller);
        });

        Platform.runLater(() -> mainScreen.setListContent(contentController));
    }
}
