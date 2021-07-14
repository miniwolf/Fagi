package com.fagi.conversation;

import java.io.Serializable;

/**
 * Created by costa on 13-11-2016.
 */
public record GetAllConversationDataRequest(String sender, long id) implements Serializable {
}
