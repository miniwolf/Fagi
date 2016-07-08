package com.fagi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by Marcus on 08-07-2016.
 */
public class SearchContactController {
    @FXML private Label userName;

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
