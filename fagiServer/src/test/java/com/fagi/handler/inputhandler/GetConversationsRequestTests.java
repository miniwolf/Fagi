package com.fagi.handler.inputhandler;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class GetConversationsRequestTests {
    private OutputAgent outputAgent;
    private Data data;
    private InputHandler inputHandler;
    private final Random random = new Random();
    private final RandomStringGenerator randomString = new RandomStringGenerator.Builder().get();

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        InputAgent inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.spy(OutputAgent.class);
        ConversationHandler conversationHandler = new ConversationHandler(data);
        inputHandler = new InputHandler(
                inputAgent,
                outputAgent,
                conversationHandler,
                data
        );

        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
    }

    @Test
    void userWithTwoConversations_ShouldGetTwoPlaceholderConversationsWhenNoConversationInRequest() {

        var user = new User(
                "fisk",
                "1234"
        );
        var con1 = createConversationForUser( 1, user);
        var con2 = createConversationForUser(1, user);

        when(data.getUser("fisk")).thenReturn(user);

        inputHandler.handleInput(new GetConversationsRequest(
                "fisk",
                Collections.emptyList()
        ));

        var argumentCaptor = ArgumentCaptor.forClass(Conversation.class);

        Mockito
                .verify(
                        outputAgent,
                        times(2)
                )
                .addResponse(argumentCaptor.capture());

        List<Conversation> conversations = argumentCaptor.getAllValues();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, conversations.size()),
                () -> Assertions.assertEquals(ConversationType.Placeholder, conversations.getFirst().getType()),
                () -> Assertions.assertEquals(ConversationType.Placeholder, conversations.getLast().getType()),
                () -> Assertions.assertEquals(1, conversations.getFirst().getParticipants().size()),
                () -> Assertions.assertEquals(user.getUserName(), conversations.getFirst().getParticipants().getFirst()),
                () -> Assertions.assertEquals(user.getUserName(), conversations.getLast().getParticipants().getFirst()),
                () -> Assertions.assertEquals(con1.getLastMessage(), conversations.getFirst().getLastMessage()),
                () -> Assertions.assertEquals(con2.getLastMessage(), conversations.getLast().getLastMessage()),
                () -> Assertions.assertEquals(con1.getLastMessageDate(), conversations.getFirst().getLastMessageDate()),
                () -> Assertions.assertEquals(con2.getLastMessageDate(), conversations.getLast().getLastMessageDate())
        );
    }

    private Conversation createConversationForUser(int numberOfMessages, User... participants) {
        var con = new Conversation(
                random.nextInt(100),
                randomString.generate(10),
                ConversationType.Single
        );
        for(var i = 0; i < numberOfMessages; i++) {
            con.addMessage(new TextMessage(randomString.generate(10), randomString.generate(10), con.getId()));
        }
        for (User participant : participants) {
            con.addUser(participant.getUserName());
            participant.addConversationID(con.getId());
        }
        when(data.getConversation(con.getId())).thenReturn(con);
        return con;
    }
}
