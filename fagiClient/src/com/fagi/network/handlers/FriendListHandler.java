package com.fagi.network.handlers;

import com.fagi.action.items.OpenConversation;
import com.fagi.controller.MainScreen;
import com.fagi.controller.contentList.ContactItemController;
import com.fagi.controller.contentList.ContentController;
import com.fagi.model.Friend;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

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

        try {
            ContentController contentController = new ContentController();
            FXMLLoader contentLoader = new FXMLLoader(
                mainScreen.getClass().getResource("/com/fagi/view/content/ContentList.fxml"));
            contentLoader.setController(contentController);

            //List<Friend> friends = friendList.getAccess().getData();
            //Collections.sort(friends); // TODO: Sorting here without using the sorted friends
            // TODO: This will be the same functionality as above
            Collections.sort(friendList.getAccess().getData());

            List<Parent> parents = new ArrayList<>();
            VBox contactContent = contentLoader.load();
            for (Friend friend : friendList.getAccess().getData()) {
                ContactItemController contactItemController = new ContactItemController();
                FXMLLoader loader = new FXMLLoader(
                    mainScreen.getClass().getResource("/com/fagi/view/content/Contact.fxml"));
                loader.setController(contactItemController);
                Pane pane = loader.load();

                setupItemController(friend, contactItemController);

                parents.add(pane);
                mainScreen.getFriendMapWrapper().register(friend, contactItemController, pane);
            }

            contentController.addAllToContentList(parents);
            mainScreen.setContactContentController(contentController);

            Platform.runLater(() -> mainScreen
                .setScrollPaneContent(MainScreen.PaneContent.Contacts, contactContent));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void setupItemController(Friend friend, ContactItemController controller) {
        controller.toggleStatus(friend.isOnline());
        controller.setUserName(friend.getUsername());
        controller.assign(new OpenConversation(mainScreen, controller.getUserName()));
    }

    @Override
    public Runnable getRunnable() {
        return () -> {
        };
    }
}
