package com.fagi.controller;

import com.fagi.action.Action;
import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenInvitation;
import com.fagi.controller.contentList.ContentItemController;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author miniwolf and zargess
 */
public class SearchContactController extends ContentItemController {
    private static final String fxmlResource = "/view/content/SearchContact.fxml";
    private final Action<String> action;

    public SearchContactController(
            boolean isFriend,
            MainScreen mainScreen,
            String contactUsername) {
        super(
                mainScreen.getUsername(),
                new Date(),
                new ArrayList<>() {{ add(contactUsername); }},
                fxmlResource
        );
        action = isFriend
                ? new OpenConversation(mainScreen)
                : new OpenInvitation(mainScreen);
    }

    @Override
    protected void timerCallback() {
    }

    @FXML
    protected void openConversation() {
        action.execute(getUserName().getText());
    }
}
