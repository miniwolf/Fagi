package com.fagi.model;

import java.io.Serializable;

/**
 * Created by Marcus on 08-07-2016.
 */
public class SearchUsersRequest implements Serializable {

    private String sender;
    private String searchString;

    public SearchUsersRequest(
            String sender,
            String searchString) {
        this.sender = sender;

        this.searchString = searchString;
    }

    public String getSearchString() {
        return searchString;
    }

    public String getSender() {
        return sender;
    }
}
