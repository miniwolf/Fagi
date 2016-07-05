package com.fagi.model;

import java.io.Serializable;

/**
 * Created by Marcus on 05-07-2016.
 */
public class UpdateHistoryRequest implements Serializable {

    private final long conversationID;
    private final int index;

    public UpdateHistoryRequest(long conversationID, int index) {
        this.conversationID = conversationID;
        this.index = index;
    }

    public long getId() {
        return conversationID;
    }

    public int getIndex() {
        return index;
    }
}
