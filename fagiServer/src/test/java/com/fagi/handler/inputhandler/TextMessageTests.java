package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.Unauthorized;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;

import static org.mockito.Mockito.when;

class TextMessageTests extends BaseInputHandlerTest {
    private Conversation conversation;
    private TextMessage message;

    void beforeEach() {
        message = new TextMessage(
                "Hullo",
                "sender",
                42
        );

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);

        conversation = new Conversation(
                42,
                "Some conversation",
                ConversationType.Single
        );
        conversation.addUser("sender");
        conversation.addUser("receiver");
    }

    @Test
    void handlingATextMessage_ShouldGiveTheMessageATimeStamp() {
        Timestamp oldTimeStamp = message
                .getMessageInfo()
                .getTimestamp();

        try {
            Thread.sleep(1); // wait to ensure we get different timestamps
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inputHandler.handleInput(message);

        Assertions.assertTrue(oldTimeStamp.getTime() < message
                .getMessageInfo()
                .getTimestamp()
                .getTime());
    }

    @Test
    void dataNotContainingConversationWithId_ShouldResultInNoSuchConversation() {
        inputHandler.handleInput(message);

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                NoSuchConversation.class
        );
    }

    @Test
    void sendingAMessageToConversationThatYouAreNotAParticipantOf_ShouldResultInUnauthorized() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);
        inputHandler.handleInput(new TextMessage(
                "Hello",
                "Not a participant",
                42
        ));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                Unauthorized.class
        );
    }

    @Test
    void sendingAMessageToConversation_ShouldResultInAllIsWell() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);

        inputHandler.handleInput(message);

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }

    @Test
    void whenSendingAMessageToConversation_ShouldResultInConversationHandlerQueueSizeIncrease() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);

        inputHandler.handleInput(message);

        Assertions.assertEquals(
                1,
                conversationHandler.queueSize()
        );
    }
}
