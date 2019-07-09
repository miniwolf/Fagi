package com.fagi.network.handlers;

import com.fagi.action.items.OpenConversation;
import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.ContactItemController;
import com.fagi.controller.contentList.ContentController;
import com.fagi.model.Friend;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.Parent;

/**
 * Created by Marcus on 09-07-2016.
 */
public class FriendListHandler implements Handler {
    private MainScreen mainScreen;

    public FriendListHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void handle(InGoingMessages object) {
        FriendList friendList = (FriendList) object;
        mainScreen.setFriendList(friendList);

        // Required here to get the load inside the constructor
        ContentController contentController =
            new ContentController("/view/content/ContentList.fxml");

        List<Friend> friends = friendList.getAccess().getData();
        Collections.sort(friends);

        List<Parent> parents = new ArrayList<>();
        for (Friend friend : friends) {
            ContactItemController contactItem = new ContactItemController();

            setupItemController(friend, contactItem);
            parents.add(contactItem);
            mainScreen.getFriendMapWrapper().register(friend, contactItem, contactItem);
        }

        contentController.addAllToContentList(parents);
        mainScreen.setContactContentController(contentController);

        Platform.runLater(() -> mainScreen
            .setScrollPaneContent(MainScreen.PaneContent.Contacts, contentController));
    }

    private void setupItemController(Friend friend, ContactItemController controller) {
        controller.toggleStatus(friend.isOnline());
        controller.setUserName(friend.getUsername());
        controller.getActionable()
                  .assign(new OpenConversation(mainScreen, controller.getUserName()));
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
