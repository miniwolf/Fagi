/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items.contentlist;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.SearchContactController;
import com.fagi.controller.contentList.ContentController;

import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Creates the element and stores it in the mainscreen's current PaneContent element.
 *
 * @author miniwolf
 */
public class CreateSearchList implements Action {
    private MainScreen mainScreen;
    private List<String> usernames;

    public CreateSearchList(MainScreen mainScreen, List<String> usernames) {
        this.mainScreen = mainScreen;
        this.usernames = usernames;
    }

    @Override
    public void execute() {
        ContentController contentController = new ContentController();
        FXMLLoader contentLoader = new FXMLLoader(
            mainScreen.getClass().getResource("/com/fagi/view/content/SearchContent.fxml"));
        contentLoader.setController(contentController);
        VBox searchContent;
        try {
            searchContent = contentLoader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return;
        }

        usernames.forEach(username -> {
            SearchContactController controller = new SearchContactController(false, mainScreen);
            FXMLLoader loader = new FXMLLoader(
                mainScreen.getClass().getResource("/com/fagi/view/content/SearchContact.fxml"));
            loader.setController(controller);
            HBox searchContact;
            try {
                searchContact = loader.load();
                controller.setUserName(username);
                contentController.addToContentList(searchContact);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        });

        Platform.runLater(() -> mainScreen.setListContent(searchContent));
    }
}
