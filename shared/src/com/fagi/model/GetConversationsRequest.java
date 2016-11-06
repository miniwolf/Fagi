package com.fagi.model;

import java.io.Serializable;

/**
 * Created by Marcus on 06-11-2016.
 */
public class GetConversationsRequest implements Serializable {
    private final String userName;

    public GetConversationsRequest(String userName) { this.userName = userName; }

    public String getUserName() {
        return userName;
    }
}
