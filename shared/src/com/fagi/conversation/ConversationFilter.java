package com.fagi.conversation;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by costa on 12-11-2016.
 */
public class ConversationFilter implements Serializable {
    private final long id;
    private final Date lastMessageDate;

    public ConversationFilter(long id, Date lastMessageDate) {
        this.id = id;
        this.lastMessageDate = lastMessageDate;
    }

    public long getId() {
        return id;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }
}
