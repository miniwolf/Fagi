package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public class GetFriendListRequest implements Serializable {
    private final String sender;

    public GetFriendListRequest(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }
}
