package com.fagi.network.handlers;

import com.fagi.controller.MainScreen;
import com.fagi.controller.SearchContactController;
import com.fagi.controller.SearchContentController;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.network.InputHandler;
import com.fagi.network.handlers.container.Container;
import com.fagi.network.handlers.container.DefaultContainer;
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
    private Container container = new DefaultContainer();
    private Runnable runnable = new DefaultThreadHandler(container, this);

    public SearchHandler(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        container.setThread(runnable);
        InputHandler.register(SearchUsersResult.class, container);
    }

    @Override
    public void handle(InGoingMessages object) {
        SearchUsersResult result = (SearchUsersResult)object;

        try {
            SearchContentController searchContentController = new SearchContentController();
            FXMLLoader contentLoader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/SearchContent.fxml"));
            contentLoader.setController(searchContentController);
            VBox searchContent = contentLoader.load();

            for (String username : result.getData()) {
                SearchContactController controller = new SearchContactController();
                FXMLLoader loader = new FXMLLoader(mainScreen.getClass().getResource("/com/fagi/view/SearchContact.fxml"));
                loader.setController(controller);
                HBox searchContact = loader.load();

                controller.setUserName(username);

                searchContentController.addToContentList(searchContact);
            }

            Platform.runLater(() ->  mainScreen.setScrollPaneContent(searchContent));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }
}
