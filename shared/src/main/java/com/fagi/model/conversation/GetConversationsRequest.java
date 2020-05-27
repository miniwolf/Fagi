package com.fagi.model.conversation;

import com.fagi.conversation.ConversationFilter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 06-11-2016.
 */
public class GetConversationsRequest implements Serializable {
    private final String userName;
    private final List<ConversationFilter> filters;

    public GetConversationsRequest(
            String userName,
            List<ConversationFilter> filters) {
        this.userName = userName;
        this.filters = filters;
    }

    public String getUserName() {
        return userName;
    }

    public List<ConversationFilter> getFilters() {
        return filters;
    }
}
