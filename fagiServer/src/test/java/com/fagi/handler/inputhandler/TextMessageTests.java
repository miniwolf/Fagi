package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.Unauthorized;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Timestamp;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class TextMessageTests {
    private OutputAgent outputAgent;
    private Data data;

    private InputHandler inputHandler;
    private Conversation conversation;
    private TextMessage message;

    @BeforeEach
    void setup() {
        message = new TextMessage("Hullo", "sender", 42);

        data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.spy(OutputAgent.class);
        ConversationHandler conversationHandler = new ConversationHandler(data);
        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);

        conversation = new Conversation(42, "Some conversation", ConversationType.Single);
        conversation.addUser("sender");
        conversation.addUser("receiver");
    }

    @Test
    void handlingATextMessage_ShouldGiveTheMessageATimeStamp() {
        Timestamp oldTimeStamp = message.getMessageInfo().getTimestamp();

        try {
            Thread.sleep(1); // wait to ensure we get different timestamps
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inputHandler.handleInput(message);

        Assertions.assertTrue(oldTimeStamp.getTime() < message.getMessageInfo().getTimestamp().getTime());
    }

    @Test
    void dataNotContainingConversationWithId_ShouldResultInNoSuchConversation() {
        inputHandler.handleInput(message);

        var argumentCaptor = ArgumentCaptor.forClass(NoSuchConversation.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof NoSuchConversation);
    }

    @Test
    void sendingAMessageToConversationThatYouAreNotAParticipantOf_ShouldResultInUnauthorized() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);
        inputHandler.handleInput(new TextMessage("Hello", "Not a participant", 42));

        var argumentCaptor = ArgumentCaptor.forClass(Unauthorized.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof Unauthorized);
    }

    @Test
    void sendingAMessageToConversation_ShouldResultInAllIsWell() {
        when(data.getConversation(Mockito.anyLong())).thenReturn(conversation);

        inputHandler.handleInput(message);

        var argumentCaptor = ArgumentCaptor.forClass(AllIsWell.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof AllIsWell);
    }
}
