package com.fagi.handler;

import com.fagi.model.Data;
import com.fagi.model.Login;
import com.fagi.model.User;
import com.fagi.model.UserLoggedIn;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserOnline;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class LoginServerTests {
    private Data data;
    private InputHandler inputhandler;
    private OutputAgent outputAgent;
    private InputAgent inputAgent;

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.mock(OutputAgent.class);
        var conversationHandler = new ConversationHandler(data);
        inputhandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
    }

    @Test
    void alreadyOnlineUserLoginAgain_ShouldGetUserOnlineResponse() {
        var loginRequest = new Login("bob", "123");

        when(data.userLogin(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(new UserOnline());

        inputhandler.handleInput(loginRequest);

        var argumentCaptor = ArgumentCaptor.forClass(UserOnline.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());
    }

    @Test
    void loginAttemptForNotOnlineUser_ShouldGetAllIsWellResponse() {
        var loginRequest = new Login("bob", "123");
        var user = new User("bob", "123");

        when(data.userLogin(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(new AllIsWell());
        when(data.getUser(Mockito.anyString())).thenReturn(user);
        when(inputAgent.getUsername()).thenReturn("bob");

        inputhandler.handleInput(loginRequest);

        var argumentCaptor = ArgumentCaptor.forClass(AllIsWell.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());
    }

    @Test
    void userThatLoginHasOneOnlineFriend_ShouldResultInFriendGettingNotification() {
        var loginRequest = new Login("bob", "123");
        var user = new User("bob", "123");
        var friend = new User("friend", "123");
        var otherFriend = new User("otherFriend", "123");
        user.addFriend(friend);
        user.addFriend(otherFriend);
        var otherFriendOutputAgent = Mockito.mock(OutputAgent.class);

        when(data.userLogin(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(new AllIsWell());
        when(data.isUserOnline(friend.getUserName())).thenReturn(true);
        when(data.getUser(Mockito.anyString())).thenReturn(user);
        when(inputAgent.getUsername()).thenReturn(user.getUserName());
        when(data.getOutputAgent(friend.getUserName())).thenReturn(outputAgent);
        when(data.getOutputAgent(otherFriend.getUserName())).thenReturn(otherFriendOutputAgent);

        inputhandler.handleInput(loginRequest);

        var otherFriendArgumentCaptor = ArgumentCaptor.forClass(UserLoggedIn.class);
        Mockito.verify(otherFriendOutputAgent, times(0)).addMessage(otherFriendArgumentCaptor.capture());

        var argumentCaptor = ArgumentCaptor.forClass(UserLoggedIn.class);
        Mockito.verify(outputAgent, times(1)).addMessage(argumentCaptor.capture());

        Assertions.assertEquals(user.getUserName(), argumentCaptor.getValue().getUsername());
    }
}
