package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.HistoryUpdates;
import com.fagi.model.User;
import com.fagi.model.conversation.UpdateHistoryRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.*;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class UpdateHistoryRequestTests {
    private OutputAgent outputAgent;
    private Data data;
    private InputHandler inputHandler;

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.spy(OutputAgent.class);
        ConversationHandler conversationHandler = new ConversationHandler(data);
        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
    }

    @Test
    void givenUserDoesNotExist_ThenOutputNoSuchUser() {
        when(data.getUser(anyString())).thenReturn(null);

        inputHandler.handleInput(new UpdateHistoryRequest("username", 42, new Date()));

        OutputAgentTestUtil.verifyOutputAgentResponseClass(outputAgent, NoSuchUser.class);
    }

    @Test
    void givenUserNotHaveAccessToConversation_ThenOutputUnauthorized() {
        var user = new User("username", "password");

        when(data.getUser(anyString())).thenReturn(user);

        inputHandler.handleInput(new UpdateHistoryRequest("username", 42, new Date()));

        OutputAgentTestUtil.verifyOutputAgentResponseClass(outputAgent, Unauthorized.class);
    }

    @Test
    void givenConversationIdNotMatchingAConversation_ThenOutputNoSuchConversation() {
        var conversationId = 42;
        var user = new User("username", "password");
        user.addConversationID(conversationId);

        when(data.getUser(anyString())).thenReturn(user);

        inputHandler.handleInput(new UpdateHistoryRequest("username", conversationId, new Date()));

        OutputAgentTestUtil.verifyOutputAgentResponseClass(outputAgent, NoSuchConversation.class);
    }

    @Test
    void givenAllMessagesAreAfterLastMessageReceived_ThenShouldReceiveAllMessages() {
        var lastMessageReceived = new Timestamp(System.currentTimeMillis() - 2000);

        var conversationId = 42;
        var user = new User("username", "password");
        var otherUser = new User("otherUsername", "password");
        user.addConversationID(conversationId);
        otherUser.addConversationID(conversationId);

        var conversation = new Conversation(conversationId, "some Name", ConversationType.Single);
        conversation.addMessage(createTextMessage(otherUser, "Message 1", conversationId, 2000));
        conversation.addMessage(createTextMessage(otherUser, "Message 2", conversationId, 3000));

        when(data.getUser(user.getUserName())).thenReturn(user);
        when(data.getConversation(conversationId)).thenReturn(conversation);

        inputHandler.handleInput(new UpdateHistoryRequest(user.getUserName(), conversationId, lastMessageReceived));

        OutputAgentTestUtil.verifyOutputAgentResponseClass(outputAgent, AllIsWell.class);

        var argumentCaptor = ArgumentCaptor.forClass(HistoryUpdates.class);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());

        var historyUpdates = argumentCaptor.getValue();
        assertAll(() -> assertEquals(conversationId, historyUpdates.id()),
                () -> assertEquals(2, historyUpdates.updates().size()),
                () -> assertEquals("Message 1", historyUpdates.updates().getFirst().data()),
                () -> assertEquals("Message 2", historyUpdates.updates().getLast().data()));
    }

    @Test
    void givenOnlyOneMessageIsAfterLastMessageReceived_ThenShouldReceiveThatMessage() {
        var lastMessageReceived = new Timestamp(System.currentTimeMillis() - 2000);

        var conversationId = 42;
        var user = new User("username", "password");
        var otherUser = new User("otherUsername", "password");
        user.addConversationID(conversationId);
        otherUser.addConversationID(conversationId);

        var conversation = new Conversation(conversationId, "some Name", ConversationType.Single);
        conversation.addMessage(createTextMessage(otherUser, "Message 1", conversationId, -5000));
        conversation.addMessage(createTextMessage(otherUser, "Message 2", conversationId, 3000));

        when(data.getUser(user.getUserName())).thenReturn(user);
        when(data.getConversation(conversationId)).thenReturn(conversation);

        inputHandler.handleInput(new UpdateHistoryRequest(user.getUserName(), conversationId, lastMessageReceived));

        OutputAgentTestUtil.verifyOutputAgentResponseClass(outputAgent, AllIsWell.class);

        var argumentCaptor = ArgumentCaptor.forClass(HistoryUpdates.class);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addResponse(argumentCaptor.capture());

        var historyUpdates = argumentCaptor.getValue();
        assertAll(() -> assertEquals(conversationId, historyUpdates.id()),
                () -> assertEquals(1, historyUpdates.updates().size()),
                () -> assertEquals("Message 2", historyUpdates.updates().getFirst().data()));
    }

    private static TextMessage createTextMessage(User user, String message, int conversationId, long offsetFromNow) {
        var result = new TextMessage(message, user.getUserName(), conversationId);
        result.getMessageInfo().setTimestamp(new Timestamp(System.currentTimeMillis() + offsetFromNow));
        return result;
    }
}
