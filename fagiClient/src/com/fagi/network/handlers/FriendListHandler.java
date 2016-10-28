package com.fagi.network.handlers;

import com.fagi.controller.ContactController;
import com.fagi.controller.ContentController;
import com.fagi.controller.MainScreen;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.FriendList;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

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
            FXMLLoader contentLoader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/ContactContent.fxml"));
            contentLoader.setController(contentController);
            VBox contactContent = contentLoader.load();

            for (String username : friendList.getAccess().getData()) {
                ContactController contactController = new ContactController();
                FXMLLoader loader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/Contact.fxml"));
                loader.setController(contactController);
                Pane pane = loader.load();

                contactController.setUserName(username);

                contentController.addToContentList(pane);
            }

            Platform.runLater(() ->  mainScreen.setScrollPaneContent(contactContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
