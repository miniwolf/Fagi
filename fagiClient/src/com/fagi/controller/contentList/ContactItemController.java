package com.fagi.controller.contentList;

import com.fagi.action.ActionableImpl;
import com.fagi.controller.MainScreen;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * @author miniwolf and zargess
 */
public class ContactItemController extends ActionableImpl<ItemActions> {
    @FXML private Label userName;
    @FXML private Label date;
    @FXML private Label lastMessage;

    private final MainScreen mainScreen;

    public ContactItemController(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @FXML
    public void initialize() {
        AddAction(ItemActions.OpenConversation, ItemActions.CreateOpenConversation(mainScreen, userName));
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    @FXML
    public void openConversation() {
        ExecuteAction(ItemActions.OpenConversation);
    }
}
