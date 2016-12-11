package com.fagi.network.handlers;

import com.fagi.controller.contentList.ContentController;
import com.fagi.controller.MainScreen;
import com.fagi.controller.SearchContactController;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.InGoingMessages;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

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
        SearchUsersResult result = (SearchUsersResult)object;

        try {
            ContentController contentController = new ContentController();
            FXMLLoader contentLoader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/SearchContent.fxml"));
            contentLoader.setController(contentController);
            VBox searchContent = contentLoader.load();

            // TODO : Do something similar with friends
            for (String username : result.getData().getUsernames()) {
                SearchContactController controller = new SearchContactController(false, mainScreen);
                FXMLLoader loader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/SearchContact.fxml"));
                loader.setController(controller);
                HBox searchContact = loader.load();

                controller.setUserName(username);

                contentController.addToContentList(searchContact);
            }

            Platform.runLater(() ->  mainScreen.setScrollPaneContent(mainScreen.getCurrentPaneContent(), searchContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
