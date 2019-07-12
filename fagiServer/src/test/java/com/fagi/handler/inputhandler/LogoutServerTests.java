package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.Logout;
import com.fagi.model.User;
import com.fagi.model.UserLoggedOut;
import com.fagi.responses.AllIsWell;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;

class LogoutServerTests {
    private Data data;
    private InputHandler inputHandler;
    private OutputAgent outputAgent;
    private InputAgent inputAgent;

    @BeforeEach
    void setup() {
        inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.mock(OutputAgent.class);
        data = Mockito.mock(Data.class);

        inputHandler = new InputHandler(inputAgent, outputAgent, new ConversationHandler(data), data);
    }

    @Test
    void whenUserLogout_UserOnlineFriendsGetsNotification() {
        User user = new User("username", "password");
        var friend = new User("friend", "123");
        var otherFriend = new User("otherUser", "123");
        user.addFriend(friend);
        user.addFriend(otherFriend);
        var otherFriendOutputAgent = Mockito.mock(OutputAgent.class);

        doReturn(user).when(data).getUser(Mockito.anyString());
        doReturn(user.getUserName()).when(inputAgent).getUsername();
        doReturn(true).when(data).isUserOnline(friend.getUserName());
        doReturn(outputAgent).when(data).getOutputAgent(friend.getUserName());
        doReturn(otherFriendOutputAgent).when(data).getOutputAgent(otherFriend.getUserName());

        inputHandler.handleInput(new Logout());

        var otherFriendArgumentCaptor = ArgumentCaptor.forClass(UserLoggedOut.class);
        Mockito.verify(otherFriendOutputAgent, times(0)).addMessage(otherFriendArgumentCaptor.capture());

        var argumentCaptor = ArgumentCaptor.forClass(UserLoggedOut.class);
        Mockito.verify(outputAgent, times(1)).addMessage(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertTrue(argumentCaptor.getValue() instanceof UserLoggedOut),
                () -> Assertions.assertEquals(user.getUserName(), argumentCaptor.getValue().getUsername())
        );
    }

    @Test
    void handlingLogoutRequest_ShouldResultInAllIsWellResponse() {
        inputHandler.handleInput(new Logout());

        var argumentCaptor = ArgumentCaptor.forClass(AllIsWell.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());

        Assertions.assertTrue(argumentCaptor.getValue() instanceof AllIsWell);
    }
}
