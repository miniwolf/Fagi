package com.fagi.handler.inputhandler;

import com.fagi.model.DeleteFriend;
import com.fagi.model.User;
import com.fagi.responses.AllIsWell;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class DeleteFriendTests extends BaseInputHandlerTest {
    private User user;

    void beforeEach() {
        user = Mockito.mock(User.class);
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
        inputHandler.handleInput(new DeleteFriend(otherUsername));

        verify(
                otherUser,
                never()
        ).removeFriend(
                eq(data),
                anyString()
        );
        verify(
                user,
                times(1)
        ).removeFriend(
                data,
                otherUsername
        );
    }

    @Test
    void whenSuccessfulRemovingFriend_ShouldGetAllIsWellResponse() {
        String username = "username";
        String otherUsername = "otherUsername";
        doReturn(user)
                .when(data)
                .getUser(username);
        doReturn(new AllIsWell())
                .when(user)
                .removeFriend(
                        data,
                        otherUsername
                );
        doReturn(username)
                .when(inputAgent)
                .getUsername();

        inputHandler.handleInput(new DeleteFriend(otherUsername));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }
}
