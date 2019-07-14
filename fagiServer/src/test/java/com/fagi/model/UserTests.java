package com.fagi.model;

import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.doReturn;

public class UserTests {
    private Data data;
    private User user;

    @BeforeEach
    void setup() {
        data = Mockito.spy(new Data());
        user = new User("username", "password");
        doReturn(new AllIsWell()).when(data).storeUser(Mockito.any());
    }

    @Test
    void sendingFriendRequestToAFriend_ShouldResultInUserExistsResponse() {
        var friend = new User("friend", "123");
        var friendReq = new FriendRequest(friend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));
        user.addFriend(friend);

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
    void sendingFriendRequestToNonFriendUser_ShouldResultInUserGettingAFriendRequest() {
        var newFriend = new User("new friend", "123");
        var friendReq = new FriendRequest(newFriend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        doReturn(newFriend).when(data).getUser(newFriend.getUserName());

        user.requestFriend(data, friendReq);

        Assertions.assertEquals(friendReq, newFriend.getFriendReq().get(0));
    }

    @Test
    void sendingFriendRequestToUserThatHaveSendAFriendRequestToThem_ShouldResultInRemovalOfBothRequests() {
        var newFriend = new User("new friend", "123");
        var firstFriendRequest = new FriendRequest(user.getUserName(), new TextMessage("Hello", newFriend.getUserName(), -1));
        var secondFriendReq = new FriendRequest(newFriend.getUserName(), new TextMessage("Hello", user.getUserName(), -1));

        doReturn(user).when(data).getUser(user.getUserName());
        doReturn(newFriend).when(data).getUser(newFriend.getUserName());

        newFriend.requestFriend(data, firstFriendRequest);

        user.requestFriend(data, secondFriendReq);

        Assertions.assertAll(
                () -> Assertions.assertFalse(user.getFriendReq().contains(firstFriendRequest)),
                () -> Assertions.assertFalse(newFriend.getFriendReq().contains(secondFriendReq))
        );
    }

    @Test
    void tryingToRemoveNonExistingFriendRequest_ShouldResultInNoSuchUserResponse() {
        Response response = user.removeFriendRequest(data, "non existing");

        Assertions.assertTrue(response instanceof NoSuchUser);
    }
}
