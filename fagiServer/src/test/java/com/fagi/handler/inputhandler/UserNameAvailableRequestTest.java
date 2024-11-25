package com.fagi.handler.inputhandler;

import com.fagi.model.User;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class UserNameAvailableRequestTest extends BaseInputHandlerTest {
    void beforeEach() {
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
