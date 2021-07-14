package com.fagi.model.conversation;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Marcus on 04-07-2016.
 */
public record CreateConversationRequest(List<String> participants) implements Serializable {
}
