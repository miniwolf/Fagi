package com.fagi.handler.inputhandler;

import com.fagi.handler.ConversationHandler;
import com.fagi.handler.InputHandler;
import com.fagi.model.Data;
import com.fagi.model.DeleteFriendRequest;
import com.fagi.model.User;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteFriendRequestTests {
    private InputHandler inputHandler;
    @Mock private OutputAgent outputAgent;
    @Mock private InputAgent inputAgent;
    @Mock private Data data;
    @Mock private User user;

    @BeforeEach
    void setup() {
        var conversationHandler = new ConversationHandler(data);

        inputHandler = new InputHandler(inputAgent, outputAgent, conversationHandler, data);
    }

    @Test
    void whenRemovingFriend_ShouldRemoveFromCorrectUser() {
        var otherUser = Mockito.mock(User.class);

        String username = "username";

        lenient()
                .doReturn(otherUser)
                .when(data)
                .getUser(anyString());
        doReturn(user)
                .when(data)
                .getUser(username);
        doReturn(username)
                .when(inputAgent)
                .getUsername();

        String otherUsername = "otherUsername";
        inputHandler.handleInput(new DeleteFriendRequest(otherUsername));

        verify(otherUser, never()).removeFriendRequest(eq(data), anyString());
        verify(user, times(1)).removeFriendRequest(data, otherUsername);
    }
}
