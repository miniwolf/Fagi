package com.fagi.network.handlers;

import com.fagi.controller.ContentController;
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
            FXMLLoader contentLoader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/SearchContent.fxml"));
            contentLoader.setController(contentController);
            VBox searchContent = contentLoader.load();

            for (String username : result.getData()) {
                SearchContactController controller = new SearchContactController();
                FXMLLoader loader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/content/SearchContact.fxml"));
                loader.setController(controller);
                HBox searchContact = loader.load();

                controller.setUserName(username);

                contentController.addToContentList(searchContact);
            }

            Platform.runLater(() ->  mainScreen.setScrollPaneContent(searchContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return () -> { };
    }
}
