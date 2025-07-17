package com.fagi.model;

import com.fagi.BaseFagiTest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.util.DataTestUtil;
import com.fagi.util.TestHelper;
import com.fagi.utility.JsonFileOperations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserTests extends BaseFagiTest {
    private Data data;
    private User user;
    private User secondUser;

    @BeforeEach
    void setup() {
        data = Mockito.mock(Data.class);
        user = new User(
                "username",
                "password"
        );
        secondUser = new User(
                "second user",
                "123"
        );
    }

    @Test
    void sendingFriendRequestToAFriend_ShouldResultInUserExistsResponse() {
        user.addFriend(secondUser);
        var friendReq = new FriendRequest(
                secondUser.getUserName(),
                new TextMessage(
                        "Hello",
                        user.getUserName(),
                        -1
                )
        );

        Response response = user.requestFriend(
                data,
                friendReq
        );

        assertInstanceOf(
                UserExists.class,
                response
        );
    }

    @Test
    void sendingFriendRequestToNonExistingUser_ShouldResultInNoSuchUserResponse() {
        var friendReq = new FriendRequest(
                "non existing user",
                new TextMessage(
                        "Hello",
                        user.getUserName(),
                        -1
                )
        );

        Response response = user.requestFriend(
                data,
                friendReq
        );

        assertInstanceOf(
                NoSuchUser.class,
                response
        );
    }

    @Test
    void sendingFriendRequestToUserNotInFriendRequestList_ShouldResultInUserGettingAFriendRequest() {
        when(data.getUser(secondUser.getUserName())).thenReturn(secondUser);

        var friendReq = new FriendRequest(
                secondUser.getUserName(),
                new TextMessage(
                        "Hello",
                        user.getUserName(),
                        -1
                )
        );

        when(data.storeUser(any(User.class))).thenReturn(new AllIsWell());

        var response = user.requestFriend(
                data,
                friendReq
        );

        ArgumentCaptor<User> userArgumentCaptor = DataTestUtil.verifyStoreUserCalled(
                data,
                1
        );

        List<FriendRequest> friendRequests = secondUser.getFriendReq();
        Assertions.assertAll(
                () -> assertEquals(
                        friendReq,
                        friendRequests.getFirst()
                ),
                () -> assertEquals(
                        secondUser,
                        userArgumentCaptor.getValue()
                ),
                () -> assertInstanceOf(
                        AllIsWell.class,
                        response
                )
        );
    }

    @Test
    void sendingFriendRequestToUserInFriendRequestList_ShouldResultInRemovalOfBothRequests() {
        when(data.getUser(user.getUserName())).thenReturn(user);
        when(data.getUser(secondUser.getUserName())).thenReturn(secondUser);

        var firstFriendRequest = new FriendRequest(
                user.getUserName(),
                new TextMessage(
                        "Hello",
                        secondUser.getUserName(),
                        -1
                )
        );

        secondUser.requestFriend(
                data,
                firstFriendRequest
        );

        var secondFriendReq = new FriendRequest(
                secondUser.getUserName(),
                new TextMessage(
                        "Hello",
                        user.getUserName(),
                        -1
                )
        );

        user.requestFriend(
                data,
                secondFriendReq
        );

        List<FriendRequest> userFriendRequests = user.getFriendReq();
        List<FriendRequest> newFriendFriendRequests = secondUser.getFriendReq();

        assertAll(
                () -> assertFalse(userFriendRequests.contains(firstFriendRequest)),
                () -> assertFalse(newFriendFriendRequests.contains(secondFriendReq))
        );
    }

    @Test
    void sendingFriendRequestToUserInFriendRequestList_ShouldResultInUsersBecomingFriends() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            mockJsonFileOperations
                    .when(() -> JsonFileOperations.storeObjectToFile(
                            any(),
                            any(),
                            any()
                    ))
                    .thenAnswer(i -> null);
            var data = Mockito.spy(new Data());
            when(data.getUser(user.getUserName())).thenReturn(user);
            when(data.getUser(secondUser.getUserName())).thenReturn(secondUser);

            var firstFriendRequest = new FriendRequest(
                    user.getUserName(),
                    new TextMessage(
                            "Hello",
                            secondUser.getUserName(),
                            -1
                    )
            );
            var secondFriendReq = new FriendRequest(
                    secondUser.getUserName(),
                    new TextMessage(
                            "Hello",
                            user.getUserName(),
                            -1
                    )
            );

            secondUser.requestFriend(
                    data,
                    firstFriendRequest
            );

            var response = user.requestFriend(
                    data,
                    secondFriendReq
            );

            Assertions.assertAll(
                    () -> Assertions.assertInstanceOf(
                            AllIsWell.class,
                            response
                    ),
                    () -> Assertions.assertEquals(
                            1,
                            user
                                    .getFriends()
                                    .size()
                    ),
                    () -> Assertions.assertEquals(
                            1,
                            user
                                    .getFriends()
                                    .size()
                    )
            );
        }
    }

    @Test
    void twoDifferentUsersSendingFriendRequestToSameUser_ShouldResultInUserHavingTwoRequestsAndNoFriends() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            mockJsonFileOperations
                    .when(() -> JsonFileOperations.storeObjectToFile(
                            any(),
                            any(),
                            any()
                    ))
                    .thenAnswer(i -> null);
            var data = Mockito.spy(new Data());
            var thirdUser = new User(
                    "third",
                    "3"
            );

            when(data.getUser(user.getUserName())).thenReturn(user);
            when(data.getUser(secondUser.getUserName())).thenReturn(secondUser);
            when(data.getUser(secondUser.getUserName())).thenReturn(secondUser);
            when(data.getUser(thirdUser.getUserName())).thenReturn(thirdUser);

            var firstFriendRequest = new FriendRequest(
                    user.getUserName(),
                    new TextMessage(
                            "Hello",
                            secondUser.getUserName(),
                            -1
                    )
            );
            var secondFriendReq = new FriendRequest(
                    user.getUserName(),
                    new TextMessage(
                            "Hello",
                            thirdUser.getUserName(),
                            -1
                    )
            );

            thirdUser.requestFriend(
                    data,
                    new FriendRequest(
                            secondUser.getUserName(),
                            new TextMessage(
                                    "Hello",
                                    thirdUser.getUserName(),
                                    -1
                            )
                    )
            );

            secondUser.requestFriend(
                    data,
                    firstFriendRequest
            );

            var response = thirdUser.requestFriend(
                    data,
                    secondFriendReq
            );

            /*Mockito
                    .verify(
                            data,
                            never()
                    )
                    .makeFriends(
                            any(),
                            any()
                    );*/

            Assertions.assertAll(
                    () -> Assertions.assertInstanceOf(
                            AllIsWell.class,
                            response
                    ),
                    () -> Assertions.assertEquals(
                            0,
                            user
                                    .getFriends()
                                    .size(),
                            "User should have no friends :("
                    ),
                    () -> Assertions.assertEquals(
                            0,
                            secondUser
                                    .getFriends()
                                    .size(),
                            "SecondUser should have no friends :("
                    ),
                    () -> Assertions.assertEquals(
                            0,
                            thirdUser
                                    .getFriends()
                                    .size(),
                            "ThirdUser should have no friends :("
                    ),
                    () -> Assertions.assertEquals(
                            2,
                            user
                                    .getFriendReq()
                                    .size(),
                            "User should have 2 friend requests"
                    )
            );
        }
    }

    @Test
    void tryingToRemoveNonExistingFriendRequest_ShouldResultInNoSuchUserResponse() {
        Response response = user.removeFriendRequest(
                data,
                "non existing"
        );

        assertInstanceOf(
                NoSuchUser.class,
                response
        );
    }

    @Test
    void removingFriendRequest_FriendRequestRemovedAndAllIsWellResponse() {
        when(data.storeUser(any())).thenReturn(new AllIsWell());

        var senderUsername = "potential friend";
        var friendReq1 = new FriendRequest(
                user.getUserName(),
                new TextMessage(
                        "Hullo le dud",
                        "some weird sender",
                        43
                )
        );
        var friendReq2 = new FriendRequest(
                user.getUserName(),
                new TextMessage(
                        "Hullo me friend",
                        senderUsername,
                        42
                )
        );
        List<FriendRequest> friendRequestList = user.getFriendReq();
        friendRequestList.add(friendReq1);
        friendRequestList.add(friendReq2);

        Response response = user.removeFriendRequest(
                data,
                senderUsername
        );

        assertAll(
                () -> assertEquals(
                        1,
                        user
                                .getFriendReq()
                                .size()
                ),
                () -> assertEquals(
                        "some weird sender",
                        user
                                .getFriendReq()
                                .getFirst()
                                .message()
                                .getMessageInfo()
                                .getSender()
                ),
                () -> assertInstanceOf(
                        AllIsWell.class,
                        response
                )
        );
    }

    @Test
    void whenRequestingFriendTwice_ShouldResultInUserExistsResponse() {
        when(data.getUser(secondUser.getUserName())).thenReturn(secondUser);

        var friendRequest = new FriendRequest(
                secondUser.getUserName(),
                new TextMessage(
                        "Hello",
                        user.getUserName(),
                        -1
                )
        );

        user.requestFriend(
                data,
                friendRequest
        );

        Response response = user.requestFriend(
                data,
                friendRequest
        );

        assertInstanceOf(
                UserExists.class,
                response
        );
    }

    @Test
    void whenTryingToRemoveUserThatIsNotAFriend_ShouldResultInUserExistsResponse() {
        Response response = user.removeFriend(
                data,
                secondUser.getUserName()
        );
        assertInstanceOf(
                UserExists.class,
                response
        );
    }

    @Test
    void removingAFriend_ShouldResultInUsernameNotExistingInFriendList() {
        List<String> friends = user.getFriends();
        friends.add(secondUser.getUserName());

        user.removeFriend(
                data,
                secondUser.getUserName()
        );

        assertFalse(friends.contains(secondUser.getUserName()));
    }

    @Test
    void removingAFriend_ShouldResultInAllIsWellResponse() {
        when(data.storeUser(any())).thenReturn(new AllIsWell());
        user
                .getFriends()
                .add(secondUser.getUserName());

        Response response = user.removeFriend(
                data,
                secondUser.getUserName()
        );

        assertInstanceOf(
                AllIsWell.class,
                response
        );
    }

    @Test
    void userIsNotEqualToADifferentClass() {
        var user = new User(
                "uname",
                "pword"
        );
        Assertions.assertNotEquals(
                "test",
                user
        );
    }

    @Test
    void callingHashCodeMultipleTimes_ShouldResultInTheSameHashCodeEveryTime() {
        var user = new User(
                TestHelper.getSaltString(10),
                TestHelper.getSaltString(15)
        );

        var hash1 = user.hashCode();
        var hash2 = user.hashCode();

        Assertions.assertEquals(
                hash1,
                hash2
        );
    }

    @Test
    void callingHashCodeOnDifferentObjects_ShouldNotAlwaysHaveEqualHashCodes() {
        var alice = new User(
                "alice",
                "()pen)!"
        );
        var bob = new User(
                "bob",
                "qui£12!"
        );

        Assertions.assertNotEquals(
                alice.hashCode(),
                bob.hashCode()
        );
    }

    @Test
    void differentUsersAreNotEqual() {
        var alice = new User(
                "alice",
                "()pen)!"
        );
        alice.addConversationID(2);
        alice.addFriend(new User(
                "charles",
                "1234"
        ));
        alice.requestFriend(
                data,
                new FriendRequest(
                        "bob",
                        new TextMessage(
                                "asd",
                                "bob",
                                5
                        )
                )
        );
        var bob = new User(
                "bob",
                "qui£12!"
        );

        Assertions.assertNotEquals(
                alice,
                bob
        );
    }

    @Test
    void userNotEqualWithNull() {
        Assertions.assertFalse(user.equals(null));
    }

    @Test
    void userNotEqualToDifferentClass() {
        Assertions.assertNotEquals(
                user,
                "dummy"
        );
    }

    @Test
    void userNotEqualToUserWithDifferentFriends() {
        var user1 = new User(
                "charles",
                "1234"
        );
        user1.addFriend(new User(
                "Erik",
                "1234"
        ));
        var user2 = new User(
                "charles",
                "1234"
        );
        user2.addFriend(new User(
                "eva",
                "1234"
        ));

        Assertions.assertNotEquals(
                user1,
                user2
        );
    }

    @Test
    void userNotEqualToUserWithDifferentConversations() {
        var user1 = new User(
                "charles",
                "1234"
        );
        user1.addConversationID(1);
        var user2 = new User(
                "charles",
                "1234"
        );
        user2.addConversationID(2);

        Assertions.assertNotEquals(
                user1,
                user2
        );
    }
}
