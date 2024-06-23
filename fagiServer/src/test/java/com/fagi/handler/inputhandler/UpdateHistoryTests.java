package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.conversation.UpdateHistoryRequest;
import com.fagi.responses.NoSuchUser;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UpdateHistoryTests {
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
    void unknownUser_ShouldResultInNoSuchUserResponse() {
        when(data.getUser(anyString())).thenReturn(null);

        inputHandler.handleInput(new UpdateHistoryRequest("not existing user", 42, new Date()));

        OutputAgentTestUtil.verifyOutputAgentResponseClass(outputAgent, NoSuchUser.class);
    }
}
