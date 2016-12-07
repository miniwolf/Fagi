package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.OpenConversation;
import com.fagi.controller.MainScreen;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author miniwolf and zargess
 */
public class ContactItemController extends ActionableImpl {
    @FXML private Label userName;
    @FXML private Label date;
    @FXML private Label lastMessage;

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public Label getUserName() {
        return userName;
    }

    @FXML
    public void openConversation() {
        action.Execute();
    }
}
