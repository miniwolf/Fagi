package com.fagi.handler.inputhandler;

import com.fagi.model.Friend;
import com.fagi.model.GetFriendListRequest;
import com.fagi.model.User;
import com.fagi.model.messages.lists.FriendList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetFriendListRequestTests extends BaseInputHandlerTest {
    @Captor private ArgumentCaptor<FriendList> friendListArgumentCaptor;
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
        verify(
                outputAgent,
                times(1)
        ).addResponse(friendListArgumentCaptor.capture());
        assertNotNull(friendListArgumentCaptor.getValue());
    }

    @Test
    void whenUserHasNoFriends_ThenFriendListIsEmpty() {
        inputHandler.handleInput(new GetFriendListRequest("sender"));
        verify(
                outputAgent,
                times(1)
        ).addResponse(friendListArgumentCaptor.capture());
        FriendList friendList = friendListArgumentCaptor.getValue();
        assumeFalse(isNull(friendList));
        assertTrue(getFriendListData(friendList).isEmpty());
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

        verify(
                outputAgent,
                times(1)
        ).addResponse(friendListArgumentCaptor.capture());

        FriendList friendList = friendListArgumentCaptor.getValue();
        assumeFalse(isNull(friendList));
        assertEquals(
                2,
                getFriendListData(friendList).size()
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

        verify(
                outputAgent,
                times(1)
        ).addResponse(friendListArgumentCaptor.capture());

        FriendList friendList = friendListArgumentCaptor.getValue();
        assumeFalse(isNull(friendList));
        assumeTrue(getFriendListData(friendList).size() == 1);
        assertTrue(getFriendListData(friendList)
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

        verify(
                outputAgent,
                times(1)
        ).addResponse(friendListArgumentCaptor.capture());

        FriendList friendList = friendListArgumentCaptor.getValue();
        assumeFalse(isNull(friendList));
        assumeTrue(getFriendListData(friendList).size() == 1);
        assertFalse(getFriendListData(friendList)
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
