package com.fagi.model;

import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserTests {
    @Mock private Data data;
    private User user;
    private User secondUser;

    @BeforeEach
    void setup() {
        user = new User("username", "password");
        secondUser = new User("second user", "123");
        doReturn(user)
                .when(data)
                .getUser(user.getUserName());
        doReturn(secondUser)
                .when(data)
                .getUser(secondUser.getUserName());
        doReturn(new AllIsWell())
                .when(data)
                .storeUser(Mockito.any());
    }

    @Test
    void sendingFriendRequestToAFriend_ShouldResultInUserExistsResponse() {
        user.addFriend(secondUser);
        var friendReq = new FriendRequest(secondUser.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        Response response = user.requestFriend(data, friendReq);

        assertTrue(response instanceof UserExists);
    }

    @Test
    void sendingFriendRequestToNonExistingUser_ShouldResultInNoSuchUserResponse() {
        var friendReq = new FriendRequest("non existing user", new TextMessage("Hello", user.getUserName(), -1));

        Response response = user.requestFriend(data, friendReq);

        assertTrue(response instanceof NoSuchUser);
    }

    @Test
    void sendingFriendRequestToUserNotInFriendRequestList_ShouldResultInUserGettingAFriendRequest() {
        var friendReq = new FriendRequest(secondUser.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        user.requestFriend(data, friendReq);

        List<FriendRequest> friendRequests = secondUser.getFriendReq();
        assertEquals(friendReq, friendRequests.get(0));
    }

    @Test
    void sendingFriendRequestToUserInFriendRequestList_ShouldResultInRemovalOfBothRequests() {
        var firstFriendRequest = new FriendRequest(user.getUserName(),
                                                   new TextMessage("Hello", secondUser.getUserName(), -1)
        );
        var secondFriendReq = new FriendRequest(secondUser.getUserName(),
                                                new TextMessage("Hello", user.getUserName(), -1)
        );

        secondUser.requestFriend(data, firstFriendRequest);

        user.requestFriend(data, secondFriendReq);
        List<FriendRequest> userFriendRequests = user.getFriendReq();
        List<FriendRequest> newFriendFriendRequests = secondUser.getFriendReq();

        assertAll(() -> assertFalse(userFriendRequests.contains(firstFriendRequest)),
                  () -> assertFalse(newFriendFriendRequests.contains(secondFriendReq))
        );
    }

    @Test
    void sendingFriendRequestToUserInFriendRequestList_ShouldResultInUsersBecomingFriends() {
        var firstFriendRequest = new FriendRequest(user.getUserName(),
                                                   new TextMessage("Hello", secondUser.getUserName(), -1)
        );
        var secondFriendReq = new FriendRequest(secondUser.getUserName(),
                                                new TextMessage("Hello", user.getUserName(), -1)
        );

        secondUser.requestFriend(data, firstFriendRequest);

        user.requestFriend(data, secondFriendReq);

        verify(data, times(1)).makeFriends(user, secondUser);
    }

    @Test
    void tryingToRemoveNonExistingFriendRequest_ShouldResultInNoSuchUserResponse() {
        Response response = user.removeFriendRequest(data, "non existing");

        assertTrue(response instanceof NoSuchUser);
    }

    @Test
    void removingFriendRequest_FriendRequestRemovedAndAllIsWellResponse() {
        when(data.storeUser(any())).thenReturn(new AllIsWell());

        var senderUsername = "potential friend";
        var friendReq = new FriendRequest(user.getUserName(), new TextMessage("Hullo me friend", senderUsername, 42));
        List<FriendRequest> friendRequestList = user.getFriendReq();
        friendRequestList.add(friendReq);

        Response response = user.removeFriendRequest(data, senderUsername);

        assertAll(() -> assertTrue(friendRequestList.isEmpty()), () -> assertTrue(response instanceof AllIsWell));
    }

    @Test
    void whenRequestingFriendTwice_ShouldResultInUserExistsResponse() {
        var friendRequest = new FriendRequest(secondUser.getUserName(),
                                              new TextMessage("Hello", user.getUserName(), -1)
        );

        user.requestFriend(data, friendRequest);

        Response response = user.requestFriend(data, friendRequest);

        assertTrue(response instanceof UserExists);
    }

    @Test
    void whenTryingToRemoveUserThatIsNotAFriend_ShouldResultInUserExistsResponse() {
        Response response = user.removeFriend(data, secondUser.getUserName());
        assertTrue(response instanceof UserExists);
    }

    @Test
    void removingAFriend_ShouldResultInUsernameNotExistingInFriendList() {
        List<String> friends = user.getFriends();
        friends.add(secondUser.getUserName());

        user.removeFriend(data, secondUser.getUserName());

        assertFalse(friends.contains(secondUser.getUserName()));
    }

    @Test
    void removingAFriend_ShouldResultInAllIsWellResponse() {
        when(data.storeUser(any())).thenReturn(new AllIsWell());
        user
                .getFriends()
                .add(secondUser.getUserName());

        Response response = user.removeFriend(data, secondUser.getUserName());

        assertTrue(response instanceof AllIsWell);
    }
}
