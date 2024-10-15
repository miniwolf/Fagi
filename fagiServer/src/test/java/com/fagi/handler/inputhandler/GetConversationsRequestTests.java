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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;
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
        var con1 = createConversationForUser(user, 1);
        var con2 = createConversationForUser(user, 1);

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
        // TODO - Continue testing
    }

    private Conversation createConversationForUser(User user, int numberOfMessages) {
        var con = new Conversation(
                random.nextInt(100),
                randomString.generate(10),
                ConversationType.Single
        );
        for(var i = 0; i < numberOfMessages; i++) {
            con.addMessage(new TextMessage(randomString.generate(10), randomString.generate(10), con.getId()));
        }
        user.addConversationID(con.getId());
        when(data.getConversation(con.getId())).thenReturn(con);
        return con;
    }
}
