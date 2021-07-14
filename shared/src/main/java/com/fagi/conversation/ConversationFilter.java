package com.fagi.conversation;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by costa on 12-11-2016.
 */
public record ConversationFilter(long id, Date lastMessageDate) implements Serializable {
}
