package com.fagi.handler;

import com.fagi.action.Action;
import com.fagi.action.items.contentlist.CreateSearchList;
import com.fagi.controller.MainScreen;
import com.fagi.model.Friend;
import com.fagi.model.SearchUsersRequest;
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
    private final TextField searchBox;
    private final Pane searchHeader;
    private final MainScreen mainScreen;
    private final Action<List<String>> createSearchList;

    private boolean searchEnabled;

    public Search(
            TextField searchBox,
            Pane searchHeader,
            MainScreen mainScreen) {
        this.searchBox = searchBox;
        this.searchHeader = searchHeader;
        this.mainScreen = mainScreen;
        this.createSearchList = new CreateSearchList(mainScreen, true);
        initialize();
    }

    private void initialize() {
        searchEnabled = true;
        searchBox
                .textProperty()
                .addListener((observable, oldValue, newValue) -> searchUser(newValue));
        searchBox
                .focusedProperty()
                .addListener((observable, oldValue, newValue) -> toggleFocus(oldValue));
    }

    private void searchUser(String searchString) {
        if (!searchEnabled) {
            return;
        }

        if (searchString.isEmpty()) {
            defaultFriendList();
            return;
        }

        mainScreen
                .getCommunication()
                .sendObject(new SearchUsersRequest(mainScreen.getUsername(), searchString));
    }

    private void defaultFriendList() {
        List<Friend> data = mainScreen
                .getFriendList()
                .access()
                .data();
        List<String> friendList = data
                .stream()
                .map(Friend::username)
                .collect(Collectors.toList());
        createSearchList.execute(friendList);
    }

    private void toggleFocus(Boolean focusValue) {
        if (focusValue) {
            searchHeader
                    .getStyleClass()
                    .remove("focused");
            searchBox.setPromptText("New conversation");
        } else {
            searchHeader
                    .getStyleClass()
                    .add("focused");
            if (searchBox.getScene() != null && !searchHeader
                    .getStyleClass()
                    .contains("dlrqf")) {
                // This should only be removed when clicking the x button
                defaultFriendList();
                searchHeader
                        .getStyleClass()
                        .add("dlrqf");
            }
            searchBox.setPromptText("Enter username");
        }
    }

    /**
     * When we are done searching, by clicking the x button or by calling this externally
     * will turn off the styling of the x button and change the menu style to current pane.
     */
    public void stopSearching() {
        searchEnabled = false;
        mainScreen.changeMenuStyle(mainScreen
                                           .getCurrentPaneContent()
                                           .toString());
        searchBox.setText("");
        searchHeader
                .getStyleClass()
                .remove("dlrqf");
        mainScreen.requestFocus();
        searchEnabled = true;
    }
}
