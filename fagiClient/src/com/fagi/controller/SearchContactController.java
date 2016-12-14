package com.fagi.controller;

import com.fagi.action.ActionableImpl;
import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenInvitation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author miniwolf and zargess
 */
public class SearchContactController extends ActionableImpl {
    @FXML private Label userName;

    private final boolean isFriend;
    private final MainScreen mainScreen;

    public SearchContactController(boolean isFriend, MainScreen mainScreen) {
        this.isFriend = isFriend;
        this.mainScreen = mainScreen;
    }

    @FXML
    public void initialize() {
        this.assign(isFriend ? new OpenConversation(mainScreen, userName)
                             : new OpenInvitation(mainScreen, userName));
    }

    @FXML
    public void openConversation() {
        action.execute();
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
