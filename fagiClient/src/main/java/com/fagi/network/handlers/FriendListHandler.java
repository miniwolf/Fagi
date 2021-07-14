package com.fagi.network.handlers;

import com.fagi.action.items.OpenConversation;
import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.ContactItemController;
import com.fagi.controller.contentList.ContentController;
import com.fagi.model.Friend;
import com.fagi.model.messages.lists.FriendList;
import javafx.application.Platform;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Marcus on 09-07-2016.
 */
public class FriendListHandler implements Handler<FriendList> {
    private MainScreen mainScreen;

    public FriendListHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(FriendList friendList) {
        mainScreen.setFriendList(friendList);

        // Required here to get the load inside the constructor
        ContentController contentController = new ContentController("/view/content/ContentList.fxml");

        List<Friend> friends = friendList.access().data();
        Collections.sort(friends);

        List<Parent> parents = new ArrayList<>();
        for (Friend friend : friends) {
            ContactItemController contactItem = setupItemController(friend, mainScreen.getUsername());

            parents.add(contactItem);
            mainScreen
                    .getFriendMapWrapper()
                    .register(friend, contactItem, contactItem);
        }

        contentController.addAllToContentList(parents);
        mainScreen.setContactContentController(contentController);

        Platform.runLater(() -> mainScreen.setScrollPaneContent(MainScreen.PaneContent.Contacts, contentController));
    }

    private ContactItemController setupItemController(
            Friend friend,
            String username) {
        return new ContactItemController(username, friend, new OpenConversation(mainScreen), new Date());
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
