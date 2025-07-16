package com.fagi.handler.inputhandler;

import com.fagi.model.Logout;
import com.fagi.model.User;
import com.fagi.model.UserLoggedOut;
import com.fagi.responses.AllIsWell;
import com.fagi.util.OutputAgentTestUtil;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class LogoutServerTests extends BaseInputHandlerTest {
    private User user;

    void beforeEach() {
        user = new User(
                "username",
                "password"
        );

        doReturn(user)
                .when(data)
                .getUser(Mockito.anyString());
        doReturn(user.getUserName())
                .when(inputAgent)
                .getUsername();
    }

    @Test
    void whenUserLogout_UserOnlineFriendsGetsNotification() {
        var friend = new User(
                "friend",
                "123"
        );
        var otherFriend = new User(
                "otherUser",
                "123"
        );
        user.addFriend(friend);
        user.addFriend(otherFriend);
        var otherFriendOutputAgent = Mockito.mock(OutputAgent.class);

        doReturn(true)
                .when(data)
                .isUserOnline(friend.getUserName());
        doReturn(outputAgent)
                .when(data)
                .getOutputAgent(friend.getUserName());
        doReturn(otherFriendOutputAgent)
                .when(data)
                .getOutputAgent(otherFriend.getUserName());

        inputHandler.handleInput(new Logout());

        var otherFriendArgumentCaptor = ArgumentCaptor.forClass(UserLoggedOut.class);
        Mockito
                .verify(
                        otherFriendOutputAgent,
                        times(0)
                )
                .addMessage(otherFriendArgumentCaptor.capture());

        var argumentCaptor = ArgumentCaptor.forClass(UserLoggedOut.class);
        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .addMessage(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(argumentCaptor.getValue()),
                () -> Assertions.assertEquals(
                        user.getUserName(),
                        argumentCaptor
                                .getValue()
                                .username()
                )
        );
    }

    @Test
    void handlingLogoutRequest_ShouldResultInAllIsWellResponse() {
        inputHandler.handleInput(new Logout());

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }

    @Test
    void handlingLogoutRequest_ShouldResultInOutputWorkerNotRunning() {
        inputHandler.handleInput(new Logout());

        Mockito
                .verify(
                        outputAgent,
                        times(1)
                )
                .stop();
    }

    @Test
    void handlingLogoutRequest_ShouldResultInInputAgentNotRunning() {
        inputHandler.handleInput(new Logout());

        Mockito
                .verify(
                        inputAgent,
                        times(1)
                )
                .stop();
    }

    @Test
    void handlingLogoutRequest_ShouldResultInCallingLogoutOnDataWithUsername() {
        var username = "bob";

        when(inputAgent.getUsername()).thenReturn(username);

        inputHandler.handleInput(new Logout());

        var argumentCaptor = ArgumentCaptor.forClass(String.class);
        Mockito
                .verify(
                        data,
                        times(1)
                )
                .userLogout(argumentCaptor.capture());

        Assertions.assertEquals(
                username,
                argumentCaptor.getValue()
        );
    }
}
