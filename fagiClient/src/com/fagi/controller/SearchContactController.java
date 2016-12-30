package com.fagi.controller;

import com.fagi.action.Actionable;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.LoadFXML;
import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenInvitation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author miniwolf and zargess
 */
public class SearchContactController extends HBox {
    @FXML private Label userName;

    private final boolean isFriend;
    private final MainScreen mainScreen;
    private final Actionable actionable = new ActionableImpl();

    public SearchContactController(boolean isFriend, MainScreen mainScreen) {
        this.isFriend = isFriend;
        this.mainScreen = mainScreen;

        new LoadFXML(this, "/com/fagi/view/content/SearchContact.fxml").execute();
    }

    @FXML
    private void initialize() {
        actionable.assign(isFriend ? new OpenConversation(mainScreen, userName)
                                   : new OpenInvitation(mainScreen, userName));
    }

    @FXML
    private void openConversation() {
        actionable.execute();
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }
}
