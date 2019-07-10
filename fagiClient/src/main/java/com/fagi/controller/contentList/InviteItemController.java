package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.model.FriendRequest;
import com.fagi.util.DateTimeUtils;
import javafx.fxml.FXML;
import com.fagi.controller.contentList.ContentItemController;

import java.util.ArrayList;
import java.util.Date;

public class InviteItemController extends ContentItemController {
    private static final String fxmlResource = "/view/content/InviteItem.fxml";
    private Action<FriendRequest> action;
    private FriendRequest request;

    public InviteItemController(
            String username,
            Action<FriendRequest> action,
            Date date,
            FriendRequest request) {
        super(username, date, new ArrayList<>() {{
            add(request.getSender());
        }}, fxmlResource);
        this.action = action;
        this.request = request;
    }

    @Override
    protected void timerCallback() {
        date.setText(DateTimeUtils.convertDate(dateInstance));
        lastMessage.applyCss();
        lastMessage.layout();
    }

    @FXML
    protected void openConversation() {
        action.execute(request);
    }
}
