package com.fagi.handler.inputhandler;

import com.fagi.model.DeleteFriendRequest;
import com.fagi.model.User;
import com.fagi.responses.AllIsWell;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DeleteFriendRequestTests extends BaseInputHandlerTest {
    private User user;

    void beforeEach() {
        user = Mockito.mock(User.class);
    }

    @Test
    void whenRemovingFriendRequest_ShouldRemoveFromCorrectUser() {
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

        verify(
                otherUser,
                never()
        ).removeFriendRequest(
                eq(data),
                anyString()
        );
        verify(
                user,
                times(1)
        ).removeFriendRequest(
                data,
                otherUsername
        );
    }

    @Test
    void whenSuccessfulRemovingFriendRequest_ShouldGetAllIsWellResponse() {
        String username = "username";
        String otherUsername = "otherUsername";
        doReturn(user)
                .when(data)
                .getUser(username);
        doReturn(new AllIsWell())
                .when(user)
                .removeFriendRequest(
                        data,
                        otherUsername
                );
        doReturn(username)
                .when(inputAgent)
                .getUsername();

        inputHandler.handleInput(new DeleteFriendRequest(otherUsername));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }
}
