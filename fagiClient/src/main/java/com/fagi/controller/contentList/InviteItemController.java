package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.model.FriendRequest;
import com.fagi.util.DateTimeUtils;

import java.util.ArrayList;
import java.util.Date;

public class InviteItemController extends TimedContentItemController<FriendRequest> {
    private static final String fxmlResource = "/view/content/InviteItem.fxml";
    private final FriendRequest request;

    public InviteItemController(
            String username,
            Action<FriendRequest> action,
            Date date,
            FriendRequest request) {
        super(username, date, new ArrayList<>() {{
            add(request.getSender());
        }}, fxmlResource, action);
        this.request = request;
    }

    @Override
    public void timerCallback() {
        date.setText(DateTimeUtils.convertDate(dateInstance));
        lastMessage.applyCss();
        lastMessage.layout();
    }

    @Override
    protected FriendRequest getData() {
        return request;
    }
}
