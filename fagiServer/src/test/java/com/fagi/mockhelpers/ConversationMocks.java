package com.fagi.mockhelpers;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.model.Data;

import java.util.Arrays;

import static org.mockito.Mockito.doReturn;

public class ConversationMocks {
    public static Conversation createConversation(
            String... participants) {
        var conversation = new Conversation(42, "Some conversation", ConversationType.Single);

        conversation
                .getParticipants()
                .addAll(Arrays.asList(participants));

        return conversation;
    }

    public static Conversation mockConversationAndRegisterInData(Data data,
                                                                 String... participants) {
        var conversation = createConversation(participants);

        doReturn(conversation)
                .when(data)
                .getConversation(conversation.getId());

        return conversation;
    }
}
