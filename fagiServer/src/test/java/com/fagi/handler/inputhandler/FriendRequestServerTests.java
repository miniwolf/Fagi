package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.FriendRequest;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.User;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendRequestServerTests {
    private InputHandler inputHandler;
    @Mock private OutputAgent outputAgent;
    @Mock private InputAgent inputAgent;
    @Mock private Data data;
    private User user;

    @BeforeEach
    void setup() {
        var conversationHandler = new ConversationHandler(data);

        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
        user = Mockito.mock(User.class);
        var username = "bob";

        doReturn(username)
                .when(user)
                .getUserName();
        doReturn(user)
                .when(data)
                .getUser(any());
        doReturn(username)
                .when(inputAgent)
                .getUsername();
    }

    @Test
    void whenRequestFriendFails_ShouldResultInResponseNotBeingAFriendList() {
        var friendReq = new FriendRequest("new friend", new TextMessage("Hullo me friend", user.getUserName(), 42));

        doReturn(new NoSuchUser())
                .when(user)
                .requestFriend(any(), any());

        inputHandler.handleInput(friendReq);

        var argumentCaptor = ArgumentCaptor.forClass(Response.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertNotNull(argumentCaptor.getValue()),
                () -> Assertions.assertTrue(argumentCaptor.getValue() instanceof NoSuchUser)
        );
    }

    @Test
    void whenRequestFriendSucceedsAndTheyAreOnline_ShouldSendFriendListToRequestedFriend() {
        var friendReq = new FriendRequest("new friend", new TextMessage("Hullo me friend", user.getUserName(), 42));
        var friendInputAgent = Mockito.mock(InputAgent.class);
        var friendInputHandler = Mockito.mock(InputHandler.class);

        doReturn(friendInputAgent)
                .when(data)
                .getInputAgent(friendReq.getFriendUsername());

        doReturn(friendInputHandler)
                .when(friendInputAgent)
                .getInputHandler();

        when(user.requestFriend(data, friendReq)).thenReturn(new AllIsWell());

        doReturn(true)
                .when(data)
                .isUserOnline(friendReq.getFriendUsername());

        inputHandler.handleInput(friendReq);

        var argumentCaptor = ArgumentCaptor.forClass(GetFriendListRequest.class);
        Mockito.verify(friendInputHandler, times(1)).handleInput(argumentCaptor.capture());

        Assertions.assertEquals(friendReq.getFriendUsername(), argumentCaptor.getValue().getSender());
    }

    @Test
    void whenRequestFriendSucceeds_ShouldResultInTheUsersFriendListAsResponse() {
        var friend = new User("friend", "123");
        var friendReq = new FriendRequest("new friend", new TextMessage("Hullo me friend", user.getUserName(), 42));

        doReturn(false)
                .when(data)
                .isUserOnline(friendReq.getFriendUsername());

        when(user.requestFriend(data, friendReq)).thenReturn(new AllIsWell());
        doReturn(Collections.singletonList(friend.getUserName())).when(user).getFriends();

        inputHandler.handleInput(friendReq);

        var argumentCaptor = ArgumentCaptor.forClass(FriendList.class);
        Mockito.verify(outputAgent, times(1)).addResponse(argumentCaptor.capture());

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, argumentCaptor.getValue().getAccess().getData().size()),
                () -> Assertions.assertEquals(friend.getUserName(), argumentCaptor.getValue().getAccess().getData().get(0).getUsername())
        );
    }
}
