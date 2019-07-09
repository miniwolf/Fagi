package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.Communication;

/**
 * Created by costa on 11-12-2016.
 */
public class SendInvitation implements Action {
    private final Communication communication;
    private final String username;
    private final TextMessage message;

    public SendInvitation(Communication communication, String username, TextMessage message) {
        this.communication = communication;
        this.username = username;
        this.message = message;
    }

    @Override
    public void execute() {
        communication.sendObject(new FriendRequest(username, message));
    }
}
