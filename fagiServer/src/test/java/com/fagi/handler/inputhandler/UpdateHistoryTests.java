package com.fagi.handler.inputhandler;

import com.fagi.model.conversation.UpdateHistoryRequest;
import com.fagi.responses.NoSuchUser;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UpdateHistoryTests extends BaseInputHandlerTest {
    void beforeEach() {
        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
    }

    @Test
    void unknownUser_ShouldResultInNoSuchUserResponse() {
        when(data.getUser(anyString())).thenReturn(null);

        inputHandler.handleInput(new UpdateHistoryRequest("not existing user", 42, new Date()));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(outputAgent, NoSuchUser.class);
    }
}
