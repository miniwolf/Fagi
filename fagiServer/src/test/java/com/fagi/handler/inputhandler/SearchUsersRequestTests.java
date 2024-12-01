package com.fagi.handler.inputhandler;

import com.fagi.model.SearchUsersRequest;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.User;
import com.fagi.responses.AllIsWell;
import com.fagi.util.OutputAgentTestUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class SearchUsersRequestTests extends BaseInputHandlerTest {
    private final User USER = new User(
            "børge",
            "1234"
    );

    void beforeEach() {
        when(data.getOutputAgent(Mockito.anyString())).thenReturn(outputAgent);
        when(inputAgent.getUsername()).thenReturn(USER.getUserName());
    }

    @Test
    void whenSearchingForUsers_ShouldAlwaysGetAllIsWellResponse() {
        inputHandler.handleInput(new SearchUsersRequest(
                USER.getUserName(),
                "fisk"
        ));

        OutputAgentTestUtil.assertOutputAgentReceivedResponseClass(
                outputAgent,
                AllIsWell.class
        );
    }

    @Test
    void whenNoUserMatchesSearchString_ShouldReturnEmptySearchResult() {
        when(data.getUserNames()).thenReturn(List.of());

        inputHandler.handleInput(new SearchUsersRequest(
                USER.getUserName(),
                "fisk"
        ));

        var result = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        SearchUsersResult.class
                )
                .access()
                .data();

        Assertions.assertTrue(result
                                      .usernames()
                                      .isEmpty());
    }

    @Test
    void whenSearchOnlyMatchesFriends_ShouldReturnEmptySearchResult() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);
        when(data.getUserNames()).thenReturn(createUserNameList());

        inputHandler.handleInput(new SearchUsersRequest(
                USER.getUserName(),
                "Ch"
        ));

        var result = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        SearchUsersResult.class
                )
                .access()
                .data();

        Assertions.assertTrue(result
                                      .usernames()
                                      .isEmpty());
    }

    @Test
    void whenSearchOnlyMatchesTheUsersUserName_ShouldReturnEmptySearchResult() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);
        when(data.getUserNames()).thenReturn(createUserNameList());

        inputHandler.handleInput(new SearchUsersRequest(
                USER.getUserName(),
                "bø"
        ));

        var result = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        SearchUsersResult.class
                )
                .access()
                .data();

        Assertions.assertTrue(result
                                      .usernames()
                                      .isEmpty());
    }

    @Test
    void whenSearchMatchesOneFriendAndOneNonFriendUser_ShouldReturnUsernameOfNonFriend() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);
        when(data.getUserNames()).thenReturn(createUserNameList());

        inputHandler.handleInput(new SearchUsersRequest(
                USER.getUserName(),
                "Vi"
        ));

        var result = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        SearchUsersResult.class
                )
                .access()
                .data();

        Assertions.assertAll(
                () -> Assertions.assertEquals(1,
                                              result
                                                      .usernames()
                                                      .size()
                ),
                () -> Assertions.assertEquals(
                        "Victor",
                        result
                                .usernames()
                                .getFirst()
                )
        );
    }

    @Test
    void whenSearchMatchesOneFriendAndTwoNonFriendUsers_ShouldReturnUsernamesOfNonFriends() {
        when(data.getUser(USER.getUserName())).thenReturn(USER);
        when(data.getUserNames()).thenReturn(createUserNameList());

        inputHandler.handleInput(new SearchUsersRequest(
                USER.getUserName(),
                "E"
        ));

        var result = OutputAgentTestUtil
                .captureResponse(
                        outputAgent,
                        SearchUsersResult.class
                )
                .access()
                .data();

        Assertions.assertAll(
                () -> Assertions.assertEquals(2,
                                              result
                                                      .usernames()
                                                      .size()
                ),
                () -> Assertions.assertEquals(
                        "Egon",
                        result
                                .usernames()
                                .getFirst()
                ),
                () -> Assertions.assertEquals(
                        "Ester",
                        result
                                .usernames()
                                .getLast()
                )
        );
    }

    private List<String> createUserNameList() {
        var friends = List.of(
                new User(
                        "Charles",
                        "1234"
                ),
                new User(
                        "Viggo",
                        "1234"
                ),
                new User(
                        "Christina",
                        "1234"
                ),
                new User(
                        "Emma",
                        "1234"
                )
        );

        var notFriendsUserNames = List.of(
                "Victor",
                "Egon",
                "Ester",
                USER.getUserName()
        );

        friends.forEach(USER::addFriend);

        var result = new ArrayList<>(notFriendsUserNames);
        result.addAll(friends
                              .stream()
                              .map(User::getUserName)
                              .toList());

        return result;
    }
}
