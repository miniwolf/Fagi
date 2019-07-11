package com.fagi.model;

import com.fagi.responses.*;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class DataTests {
    private Data data;
    private User user;
    private InputAgent inputAgent;
    private OutputAgent outputAgent;

    @BeforeEach
    void setup() {
        data = Mockito.spy(new Data());
        inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.mock(OutputAgent.class);
        user = new User("username", "password");
    }

    @Test
    void userAlreadyOnline_ShouldResultInUserOnlineResponse() {
        doReturn(true).when(data).isUserOnline(Mockito.any());

        Response response = data.userLogin(user.getUserName(), user.getPass(), outputAgent, inputAgent);

        Assertions.assertTrue(response instanceof UserOnline);
    }

    @Test
    void userDoesNotExist_ShouldResultInNoSuchUserResponse() {
        doReturn(false).when(data).isUserOnline(Mockito.any());

        Response response = data.userLogin(user.getUserName(), user.getPass(), outputAgent, inputAgent);

        Assertions.assertTrue(response instanceof NoSuchUser);
    }

    @Test
    void wrongPassword_ShouldResultInPasswordErrorResponse() {
        doReturn(false).when(data).isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        Response response = data.userLogin(user.getUserName(), "wrong password", outputAgent, inputAgent);

        Assertions.assertTrue(response instanceof PasswordError);
    }

    @Test
    void correctLogin_ShouldResultInAllIsWellResponse() {
        doReturn(false).when(data).isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        Response response = data.userLogin(user.getUserName(), user.getPass(), outputAgent, inputAgent);

        Assertions.assertTrue(response instanceof AllIsWell);
    }

    @Test
    void correctLogin_ShouldResultInInputAgentAndOutputAgentRegistered() {
        doReturn(false).when(data).isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        data.userLogin(user.getUserName(), user.getPass(), outputAgent, inputAgent);

        Assertions.assertAll(
                () -> Assertions.assertEquals(inputAgent, data.getInputAgent(user.getUserName())),
                () -> Assertions.assertEquals(outputAgent, data.getOutputAgent(user.getUserName()))
        );
    }

    @Test
    void logoutWithNullAsUsername_ShouldNotLogoutTheUser() {
        doReturn(false).when(data).isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        data.userLogin(user.getUserName(), user.getPass(), outputAgent, inputAgent);

        data.userLogout(null);

        Assertions.assertAll(
                () -> Assertions.assertEquals(inputAgent, data.getInputAgent(user.getUserName())),
                () -> Assertions.assertEquals(outputAgent, data.getOutputAgent(user.getUserName()))
        );
    }

    @Test
    void logoutWithLoggedInUser_ShouldRemoveInputAgentAndOutputAgentFromMaps() {
        doReturn(false).when(data).isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        data.userLogin(user.getUserName(), user.getPass(), outputAgent, inputAgent);

        data.userLogout(user.getUserName());

        Assertions.assertAll(
                () -> Assertions.assertNull(data.getInputAgent(user.getUserName())),
                () -> Assertions.assertNull(data.getOutputAgent(user.getUserName()))
        );
    }
}
