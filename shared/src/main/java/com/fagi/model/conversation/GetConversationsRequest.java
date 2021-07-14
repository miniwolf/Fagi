package com.fagi.model.conversation;

import com.fagi.conversation.ConversationFilter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 06-11-2016.
 */
public record GetConversationsRequest(String userName, List<ConversationFilter> filters) implements Serializable {
}
