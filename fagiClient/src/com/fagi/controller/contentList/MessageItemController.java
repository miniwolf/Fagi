package com.fagi.controller.contentList;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Date;
import java.util.List;

/**
 * @author miniwolf
 */
public class MessageItemController {
    @FXML private Label username;
    @FXML private Label date;
    @FXML private Label lastMessage;

    public void setUsers(List<String> username) {
        this.username.setText(username.toString());
    }

    public void setDate(Date date) {
        this.date.setText(date.toString());
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage.setText(lastMessage);
    }
}
