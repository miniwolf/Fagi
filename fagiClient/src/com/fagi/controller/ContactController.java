package com.fagi.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by Marcus on 09-07-2016.
 */
public class ContactController {
    @FXML private Label userName;
    @FXML private Label date;
    @FXML private Label lastMessage;

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
