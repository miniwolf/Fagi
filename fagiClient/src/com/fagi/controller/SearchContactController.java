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
    private final boolean isFriend;

    private final MainScreen mainScreen;
    @FXML private Label userName;

    public SearchContactController(boolean isFriend, MainScreen mainScreen) {
        this.isFriend = isFriend;
        this.mainScreen = mainScreen;
    }

    @FXML
    public void initialize() {
        if (isFriend) {
            this.assign(new OpenConversation(mainScreen, userName));
        } else {
            this.assign(new OpenInvitation(mainScreen, userName));
        }
    }

    public Label getUserName() {
        return userName;
    }

    @FXML
    public void openConversation() {
        action.execute();
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
