package com.fagi.controller;

import com.fagi.action.ActionableImpl;
import com.fagi.handler.OpenConversationHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author miniwolf and zargess
 */
public class SearchContactController extends ActionableImpl {
    @FXML private Label userName;

    public Label getUserName() {
        return userName;
    }

    @FXML
    public void openConversation() {
        action.Execute();
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
