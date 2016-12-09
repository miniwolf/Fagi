package com.fagi.controller;

import com.fagi.action.ActionableImpl;
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
        action.execute();
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
