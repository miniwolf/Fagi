package com.fagi.model;

import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserTests {
    @Mock
    private Data data;
    private User user;
    private User newFriend;

    @BeforeEach
    void setup() {
        user = new User("username", "password");
        newFriend = new User("new friend", "123");
        doReturn(user).when(data).getUser(user.getUserName());
        doReturn(newFriend).when(data).getUser(newFriend.getUserName());
        doReturn(new AllIsWell()).when(data).storeUser(Mockito.any());
    }

    @Test
    void sendingFriendRequestToAFriend_ShouldResultInUserExistsResponse() {
        user.addFriend(newFriend);
        var friendReq = new FriendRequest(newFriend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        Response response = user.requestFriend(data, friendReq);

        Assertions.assertTrue(response instanceof UserExists);
    }

    @Test
    void sendingFriendRequestToNonExistingUser_ShouldResultInNoSuchUserResponse() {
        var friendReq = new FriendRequest("non existing user", new TextMessage("Hello", user.getUserName(), -1));

        Response response = user.requestFriend(data, friendReq);

        Assertions.assertTrue(response instanceof NoSuchUser);
    }

    @Test
    void sendingFriendRequestToUserNotInFriendRequestList_ShouldResultInUserGettingAFriendRequest() {
        var friendReq = new FriendRequest(newFriend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        user.requestFriend(data, friendReq);

        Assertions.assertEquals(friendReq, newFriend.getFriendReq().get(0));
    }

    @Test
    void sendingFriendRequestToUserInFriendRequestList_ShouldResultInRemovalOfBothRequests() {
        var firstFriendRequest = new FriendRequest(user.getUserName(), new TextMessage("Hello", newFriend.getUserName(), -1));
        var secondFriendReq = new FriendRequest(newFriend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        newFriend.requestFriend(data, firstFriendRequest);

        user.requestFriend(data, secondFriendReq);

        Assertions.assertAll(
                () -> Assertions.assertFalse(user.getFriendReq().contains(firstFriendRequest)),
                () -> Assertions.assertFalse(newFriend.getFriendReq().contains(secondFriendReq))
        );
    }

    @Test
    void sendingFriendRequestToUserInFriendRequestList_ShouldResultInUsersBecomingFriends() {
        var firstFriendRequest = new FriendRequest(user.getUserName(), new TextMessage("Hello", newFriend.getUserName(), -1));
        var secondFriendReq = new FriendRequest(newFriend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        newFriend.requestFriend(data, firstFriendRequest);

        user.requestFriend(data, secondFriendReq);

        verify(data, times(1)).makeFriends(user, newFriend);
    }

    @Test
    void tryingToRemoveNonExistingFriendRequest_ShouldResultInNoSuchUserResponse() {
        Response response = user.removeFriendRequest(data, "non existing");

        Assertions.assertTrue(response instanceof NoSuchUser);
    }

    @Test
    void removingFriendRequest_FriendRequestRemovedAndAllIsWellResponse() {
        when(data.storeUser(any())).thenReturn(new AllIsWell());

        var senderUsername = "potential friend";
        var friendReq = new FriendRequest(user.getUserName(), new TextMessage("Hullo me friend", senderUsername, 42));
        user.getFriendReq().add(friendReq);

        Response response = user.removeFriendRequest(data, senderUsername);

        Assertions.assertAll(
                () -> Assertions.assertTrue(user.getFriendReq().isEmpty()),
                () -> Assertions.assertTrue(response instanceof AllIsWell)
        );
    }
}
