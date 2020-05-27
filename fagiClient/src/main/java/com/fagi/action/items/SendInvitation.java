package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.Communication;

/**
 * Created by costa on 11-12-2016.
 */
public class SendInvitation implements Action<TextMessage> {
    private final Communication communication;
    private final String username;

    public SendInvitation(
            Communication communication,
            String username) {
        this.communication = communication;
        this.username = username;
    }

    @Override
    public void execute(TextMessage textMessage) {
        communication.sendObject(new FriendRequest(username, textMessage));
    }
}
