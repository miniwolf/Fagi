package com.fagi.handler.inputhandler;

import com.fagi.model.Friend;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.User;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

class GetFriendListRequestTests extends BaseInputHandlerTest {
    private final List<String> friendsUsernames = new ArrayList<>();

    void beforeEach() {
        User user = Mockito.mock(User.class);

        doReturn(user)
                .when(data)
                .getUser(anyString());

        doReturn("username")
                .when(inputAgent)
                .getUsername();

        doReturn(friendsUsernames)
                .when(user)
                .getFriends();
    }

    @Test
    void gettingFriendList_NeverReturnsNull() {
        inputHandler.handleInput(new GetFriendListRequest("sender"));

        var capturedFriendList = OutputAgentTestUtil.captureResponse(
                outputAgent,
                FriendList.class
        );

        assertNotNull(capturedFriendList);
    }

    @Test
    void whenUserHasNoFriends_ThenFriendListIsEmpty() {
        inputHandler.handleInput(new GetFriendListRequest("sender"));

        var capturedFriendList = OutputAgentTestUtil.captureResponse(
                outputAgent,
                FriendList.class
        );

        assumeFalse(isNull(capturedFriendList));
        assertTrue(getFriendListData(capturedFriendList).isEmpty());
    }

    @Test
    void whenUserHasTwoFriends_ThenFriendListShouldHaveTwoFriends() {
        String friend1 = "Friend 1";
        String friend2 = "Friend 2";
        friendsUsernames.add(friend1);
        friendsUsernames.add(friend2);
        setUserOnline(
                friend1,
                false
        );
        setUserOnline(
                friend2,
                false
        );

        inputHandler.handleInput(new GetFriendListRequest("sender"));

        var capturedFriendList = OutputAgentTestUtil.captureResponse(
                outputAgent,
                FriendList.class
        );

        assumeFalse(isNull(capturedFriendList));
        assertEquals(
                2,
                getFriendListData(capturedFriendList).size()
        );
    }

    @Test
    void whenFriendIsOnline_ThenFriendInFriendListShouldShowIt() {
        String friend = "Friend";
        friendsUsernames.add(friend);
        setUserOnline(
                friend,
                true
        );

        inputHandler.handleInput(new GetFriendListRequest("sender"));

        var capturedFriendList = OutputAgentTestUtil.captureResponse(
                outputAgent,
                FriendList.class
        );

        assumeFalse(isNull(capturedFriendList));
        assumeTrue(getFriendListData(capturedFriendList).size() == 1);
        assertTrue(getFriendListData(capturedFriendList)
                           .getFirst()
                           .online());
    }

    @Test
    void whenFriendIsNotOnline_ThenFriendInFriendListShouldShowIt() {
        String friend = "Friend";
        friendsUsernames.add(friend);
        setUserOnline(
                friend,
                false
        );

        inputHandler.handleInput(new GetFriendListRequest("sender"));

        var capturedFriendList = OutputAgentTestUtil.captureResponse(
                outputAgent,
                FriendList.class
        );

        assumeFalse(isNull(capturedFriendList));
        assumeTrue(getFriendListData(capturedFriendList).size() == 1);
        assertFalse(getFriendListData(capturedFriendList)
                            .getFirst()
                            .online());
    }

    private static List<Friend> getFriendListData(FriendList friendList) {
        return friendList
                .access()
                .data();
    }

    private void setUserOnline(
            String username,
            boolean isOnline) {
        doReturn(isOnline)
                .when(data)
                .isUserOnline(username);
    }
}
