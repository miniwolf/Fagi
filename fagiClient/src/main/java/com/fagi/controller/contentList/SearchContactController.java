package com.fagi.controller.contentList;

import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenInvitation;
import com.fagi.controller.MainScreen;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author miniwolf and zargess
 */
public class SearchContactController extends ContentItemController<String> {
    private static final String fxmlResource = "/view/content/SearchContact.fxml";

    public SearchContactController(
            boolean isFriend,
            MainScreen mainScreen,
            String contactUsername) {
        super(
                mainScreen.getUsername(),
                new Date(),
                new ArrayList<>() {{ add(contactUsername); }},
                fxmlResource,
                isFriend
                        ? new OpenConversation(mainScreen)
                        : new OpenInvitation(mainScreen)
        );
    }

    @Override
    protected String getData() {
        return getUserName().getText();
    }
}
