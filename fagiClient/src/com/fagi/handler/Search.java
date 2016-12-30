package com.fagi.handler;

import com.fagi.action.items.contentlist.CreateSearchList;
import com.fagi.controller.MainScreen;
import com.fagi.model.Friend;
import com.fagi.model.SearchUsersRequest;
import com.fagi.network.handlers.FriendListHandler;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Searching from the element in top of the list content.
 *
 * @author miniwolf
 */
public class Search {
    private TextField searchBox;
    private Pane searchHeader;
    private MainScreen mainScreen;
    private boolean searching;

    public Search(TextField searchBox, Pane searchHeader, MainScreen mainScreen) {
        this.searchBox = searchBox;
        this.searchHeader = searchHeader;
        this.mainScreen = mainScreen;
        initialize();
    }

    private void initialize() {
        searchBox.textProperty()
                 .addListener((observable, oldValue, newValue) -> searchUser(newValue));
        searchBox.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) ->
                                                    toggleFocus(oldPropertyValue));
    }

    private void searchUser(String searchString) {
        if (searchString.isEmpty()) {
            if (searching) {
                defaultFriendList();
            } else {
                new FriendListHandler(mainScreen).handle(mainScreen.getFriendList());
            }
            return;
        }

        mainScreen.getCommunication()
                  .sendObject(new SearchUsersRequest(mainScreen.getUsername(), searchString));
    }

    private void defaultFriendList() {
        List<Friend> data = mainScreen.getFriendList().getAccess().getData();
        new CreateSearchList(mainScreen,
                             data.stream().map(Friend::getUsername).collect(Collectors.toList()),
                             true)
            .execute();
    }

    private void toggleFocus(Boolean focusValue) {
        if (focusValue) {
            searchHeader.getStyleClass().remove("focused");
            searchBox.setPromptText("New conversation");
        } else {
            searchHeader.getStyleClass().add("focused");
            if (!searchHeader.getStyleClass().contains("dlrqf")) {
                // This should only be removed when clicking the x button
                searching = true;
                defaultFriendList();
                searchHeader.getStyleClass().add("dlrqf");
            }
            searchBox.setPromptText("Enter username");
        }
    }

    /**
     * When we are done searching, by clicking the x button or by calling this externally
     * will turn off the styling of the x button and change the menu style to current pane.
     */
    public void stopSearching() {
        searchBox.setText("");
        searchHeader.getStyleClass().remove("dlrqf");
        mainScreen.changeMenuStyle(mainScreen.getCurrentPaneContent().toString());
    }
}
