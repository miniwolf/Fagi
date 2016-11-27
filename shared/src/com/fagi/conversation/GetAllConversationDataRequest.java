package com.fagi.conversation;

import java.io.Serializable;

/**
 * Created by costa on 13-11-2016.
 */
public class GetAllConversationDataRequest implements Serializable {
    private final String sender;
    private final long id;

    public GetAllConversationDataRequest(String sender, long id) {
        this.sender = sender;
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public long getId() {
        return id;
    }
}
