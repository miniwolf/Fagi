package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class UserNameAvailableRequestTest {
    private OutputAgent outputAgent;
    private Data data;
    private InputHandler inputHandler;

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
    void whenUserNameDoesNotMatchAUser_ShouldReturnAllIsWellResponse() {
        inputHandler.handleInput(new UserNameAvailableRequest(
                "fisk"
        ));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }

    @Test
    void whenUserNameMatchesAUser_ShouldReturnUserExistsResponse() {
        when(data.getUser("fisk")).thenReturn(new User("fisk", "1234"));

        inputHandler.handleInput(new UserNameAvailableRequest(
                "fisk"
        ));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                UserExists.class
        );
    }
}
