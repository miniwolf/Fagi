package com.fagi.network.handlers;

import com.fagi.action.items.OpenConversation;
import com.fagi.controller.contentList.ContactItemController;
import com.fagi.controller.contentList.ContentController;
import com.fagi.controller.MainScreen;
import com.fagi.model.Friend;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendList;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        FriendList friendList = (FriendList)object;
        mainScreen.setFriendList(friendList);

        try {
            ContentController contentController = new ContentController();
            FXMLLoader contentLoader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/ContentList.fxml"));
            contentLoader.setController(contentController);
            VBox contactContent = contentLoader.load();

            List<Friend> friends = friendList.getAccess().getData();
            Collections.sort(friends);

            for (Friend friend : friendList.getAccess().getData()) {
                ContactItemController contactItemController = new ContactItemController();
                FXMLLoader loader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/Contact.fxml"));
                loader.setController(contactItemController);
                Pane pane = loader.load();
                contactItemController.toggleStatus(friend.isOnline());

                contactItemController.assign(new OpenConversation(mainScreen, contactItemController.getUserName()));
                contactItemController.setUserName(friend.getUsername());
                contentController.addToContentList(pane);

                mainScreen.getFriendMapWrapper().register(friend, contactItemController, pane);
            }

            mainScreen.setContactContentController(contentController);
            Platform.runLater(() -> mainScreen.setScrollPaneContent(MainScreen.PaneContent.contacts, contactContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
