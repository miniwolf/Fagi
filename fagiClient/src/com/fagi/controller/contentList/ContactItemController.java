package com.fagi.controller.contentList;

import com.fagi.action.ActionableImpl;
import com.fagi.action.items.OpenConversation;
import com.fagi.controller.MainScreen;
import com.fagi.conversation.Conversation;
import com.fagi.model.CreateConversationRequest;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
