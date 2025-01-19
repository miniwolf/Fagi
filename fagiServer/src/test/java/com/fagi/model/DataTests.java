package com.fagi.model;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.responses.UserOnline;
import com.fagi.utility.JsonFileOperations;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class DataTests {
    private Data data;
    private User user;
    private InputAgent inputAgent;
    private OutputAgent outputAgent;

    @BeforeEach
    void setup() {
        data = Mockito.spy(new Data());
        inputAgent = Mockito.mock(InputAgent.class);
        outputAgent = Mockito.mock(OutputAgent.class);
        user = new User(
                "username",
                "password"
        );
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Test
    void userAlreadyOnline_ShouldResultInUserOnlineResponse() {
        doReturn(true)
                .when(data)
                .isUserOnline(Mockito.any());

        Response response = data.userLogin(
                user.getUserName(),
                user.getPass(),
                outputAgent,
                inputAgent
        );

        Assertions.assertInstanceOf(
                UserOnline.class,
                response
        );
    }

    @Test
    void userDoesNotExist_ShouldResultInNoSuchUserResponse() {
        doReturn(false)
                .when(data)
                .isUserOnline(Mockito.any());

        Response response = data.userLogin(
                user.getUserName(),
                user.getPass(),
                outputAgent,
                inputAgent
        );

        Assertions.assertInstanceOf(
                NoSuchUser.class,
                response
        );
    }

    @Test
    void wrongPassword_ShouldResultInPasswordErrorResponse() {
        doReturn(false)
                .when(data)
                .isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        Response response = data.userLogin(
                user.getUserName(),
                "wrong password",
                outputAgent,
                inputAgent
        );

        Assertions.assertInstanceOf(
                PasswordError.class,
                response
        );
    }

    @Test
    void correctLogin_ShouldResultInAllIsWellResponse() {
        doReturn(false)
                .when(data)
                .isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        Response response = data.userLogin(
                user.getUserName(),
                user.getPass(),
                outputAgent,
                inputAgent
        );

        Assertions.assertInstanceOf(
                AllIsWell.class,
                response
        );
    }

    @Test
    void correctLogin_ShouldResultInInputAgentAndOutputAgentRegistered() {
        doReturn(false)
                .when(data)
                .isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        data.userLogin(
                user.getUserName(),
                user.getPass(),
                outputAgent,
                inputAgent
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        inputAgent,
                        data.getInputAgent(user.getUserName())
                ),
                () -> Assertions.assertEquals(
                        outputAgent,
                        data.getOutputAgent(user.getUserName())
                )
        );
    }

    @Test
    void logoutWithNullAsUsername_ShouldNotLogoutTheUser() {
        doReturn(false)
                .when(data)
                .isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        data.userLogin(
                user.getUserName(),
                user.getPass(),
                outputAgent,
                inputAgent
        );

        data.userLogout(null);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        inputAgent,
                        data.getInputAgent(user.getUserName())
                ),
                () -> Assertions.assertEquals(
                        outputAgent,
                        data.getOutputAgent(user.getUserName())
                )
        );
    }

    @Test
    void logoutWithLoggedInUser_ShouldRemoveInputAgentAndOutputAgentFromMaps() {
        doReturn(false)
                .when(data)
                .isUserOnline(Mockito.any());
        when(data.getUser(Mockito.any())).thenReturn(user);

        data.userLogin(
                user.getUserName(),
                user.getPass(),
                outputAgent,
                inputAgent
        );

        data.userLogout(user.getUserName());

        Assertions.assertAll(
                () -> Assertions.assertNull(data.getInputAgent(user.getUserName())),
                () -> Assertions.assertNull(data.getOutputAgent(user.getUserName()))
        );
    }

    @Test
    void creatingAUserWithAnExistingUsername_ShouldResultInUserExistsResponse() {
        doReturn(new AllIsWell())
                .when(data)
                .storeUser(Mockito.any());

        data.createUser(
                user.getUserName(),
                user.getPass()
        );
        Response response = data.createUser(
                user.getUserName(),
                user.getPass()
        );

        Assertions.assertInstanceOf(
                UserExists.class,
                response
        );
    }

    @Test
    void creatingAUserWithAvailableUsername_ShouldResultInAllIsWellResponse() {
        doReturn(new AllIsWell())
                .when(data)
                .storeUser(Mockito.any());

        Response response = data.createUser(
                user.getUserName(),
                user.getPass()
        );

        Assertions.assertInstanceOf(
                AllIsWell.class,
                response
        );
    }

    @Test
    void creatingAConversationWithTwoParticipants_ShouldResultInAConversationOfTypeSingle() {
        var participants = List.of(
                "Alice",
                "Bob"
        );

        var conversation = data.createConversation(participants);

        Assertions.assertEquals(
                ConversationType.Single,
                conversation.getType()
        );
    }

    @Test
    void creatingAConversationWithThreeParticipants_ShouldResultInAConversationOfTypeMulti() {
        var participants = List.of(
                "Alice",
                "Bob",
                "Eva"
        );

        var conversation = data.createConversation(participants);

        Assertions.assertEquals(
                ConversationType.Multi,
                conversation.getType()
        );
    }

    @Test
    void creatingAConversationWithAliceAndBob_ShouldResultInNameBeingACommaSeparatedStringOfTheirNames() {
        var participants = List.of(
                "Alice",
                "Bob"
        );

        var conversation = data.createConversation(participants);

        Assertions.assertEquals(
                "Alice, Bob",
                conversation.getName()
        );
    }

    @Test
    void creatingConversationsShouldGiveAIncrementedId() {
        var participants = List.of(
                "Alice",
                "Bob"
        );

        var conversation1 = data.createConversation(participants);
        var conversation2 = data.createConversation(participants);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        0,
                        conversation1.getId()
                ),
                () -> Assertions.assertEquals(
                        1,
                        conversation2.getId()
                )
        );
    }

    @Test
    void creatingConversationWithParticipants_ShouldResultInInitialParticipantsBeingEqualToTheInput() {
        var participants = List.of(
                "Alice",
                "Bob"
        );

        var conversation = data.createConversation(participants);

        Assertions.assertEquals(
                participants,
                conversation.getParticipants()
        );
    }

    @Test
    void creatingConversation_ShouldResultInDataClassHavingStoredConversation() {
        var participants = List.of(
                "Alice",
                "Bob"
        );

        var conversation = data.createConversation(participants);

        Assertions.assertEquals(
                conversation,
                data.getConversation(conversation.getId())
        );
    }

    @Test
    void storingConversation_ShouldResultInAttemptToStoreConversationWithJsonFileOperations() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            var participants = List.of(
                    "Alice",
                    "Bob"
            );

            var conversation = data.createConversation(participants);

            data.storeConversation(conversation);

            mockJsonFileOperations.verify(() -> JsonFileOperations.storeConversation(conversation));
        }
    }

    @Test
    void loadingConversationsWithThreeConversation_ShouldResultInBeingAbleToLookupConversationsInDataAndNextConversationIdShouldBeThree() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            data.setNextConversationId(100);

            var conversation1 = new Conversation(
                    0,
                    "name1",
                    ConversationType.Single
            );
            var conversation2 = new Conversation(
                    1,
                    "name1",
                    ConversationType.Single
            );
            var conversation3 = new Conversation(
                    2,
                    "name1",
                    ConversationType.Single
            );

            mockJsonFileOperations
                    .when(JsonFileOperations::loadAllConversations)
                    .thenReturn(List.of(
                            conversation1,
                            conversation2,
                            conversation3
                    ));

            data.loadConversations();

            var conversation4 = data.createConversation(List.of(
                    "Quirkus",
                    "Viggo"
            ));

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            conversation1,
                            data.getConversation(conversation1.getId())
                    ),
                    () -> Assertions.assertEquals(
                            conversation2,
                            data.getConversation(conversation2.getId())
                    ),
                    () -> Assertions.assertEquals(
                            conversation3,
                            data.getConversation(conversation3.getId())
                    ),
                    () -> Assertions.assertEquals(
                            3,
                            conversation4.getId()
                    )
            );
        }
    }

    @Test
    void loadingConversationsWithNoConversations_ShouldResultInIdOfNextConversationBeingZero() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            mockJsonFileOperations
                    .when(JsonFileOperations::loadAllConversations)
                    .thenReturn(List.of());

            data.setNextConversationId(100);

            data.loadConversations();

            var conversation = data.createConversation(List.of(
                    "Alice",
                    "Bog"
            ));

            Assertions.assertEquals(
                    0,
                    conversation.getId()
            );
        }
    }

    @Test
    void storingUser_ShouldResultInAllIsWell() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            var user = new User(
                    "uname",
                    "pword"
            );

            var response = data.storeUser(user);

            mockJsonFileOperations.verify(
                    () -> JsonFileOperations.storeObjectToFile(
                            user,
                            JsonFileOperations.USERS_FOLDER,
                            user.getUserName()
                    ),
                    times(1)
            );

            Assertions.assertInstanceOf(
                    AllIsWell.class,
                    response
            );
        }
    }

    @Test
    void loadingUsersWithUser_ShouldResultInBeingAbleToLookUpUser() {
        try (var mockJsonFileOperation = Mockito.mockStatic(JsonFileOperations.class)) {
            var user = new User(
                    "uname",
                    "pword"
            );

            mockJsonFileOperation
                    .when(() -> JsonFileOperations.loadAllObjectsInFolder(
                            JsonFileOperations.USERS_FOLDER,
                            User.class
                    ))
                    .thenReturn(List.of(user));

            data.loadUsers();

            Assertions.assertEquals(
                    user,
                    data.getUser(user.getUserName())
            );
        }
    }

    @Test
    void loadInviteCodes_ShouldReturnResultOfJsonFileOperation() {
        try (var mockJsonFileOperation = Mockito.mockStatic(JsonFileOperations.class)) {
            var inviteCodeContainer = new InviteCodeContainer(List.of(
                    new InviteCode("1"),
                    new InviteCode("2")
            ));

            mockJsonFileOperation
                    .when(() -> JsonFileOperations.loadObjectFromFile(
                            JsonFileOperations.INVITE_CODES_FILE_PATH,
                            InviteCodeContainer.class
                    ))
                    .thenReturn(inviteCodeContainer);

            var result = data.loadInviteCodes();

            Assertions.assertEquals(
                    inviteCodeContainer,
                    result
            );
        }
    }

    @Test
    void storeInviteCodes_ShouldAttemptToStoreInviteCodesToDisk() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            var inviteCodeContainer = new InviteCodeContainer(List.of(
                    new InviteCode("1"),
                    new InviteCode("2")
            ));

            data.storeInviteCodes(inviteCodeContainer);

            mockJsonFileOperations.verify(
                    () -> JsonFileOperations.storeObjectToFile(
                            inviteCodeContainer,
                            JsonFileOperations.CONFIG_FOLDER_PATH,
                            JsonFileOperations.INVITE_CODES_FILE
                    ),
                    times(1)
            );
        }
    }

    @Test
    void logoutUserWithNotLoggedInUser_ShouldResultInPrintToSysOut() {
        var outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        var user = new User(
                "bob",
                "password"
        );

        data.userLogout(user.getUserName());

        Assertions.assertEquals(
                "Couldn't log " + user.getUserName() + " out",
                outputStreamCaptor
                        .toString()
                        .trim()
        );
    }

    @Test
    void checkingIfAnOnlineUserIsOnline_ShouldReturnTrue() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            var user = new User(
                    "bob",
                    "1234"
            );

            data.createUser(
                    user.getUserName(),
                    user.getPass()
            );

            mockJsonFileOperations.verify(() -> JsonFileOperations.storeObjectToFile(
                    user,
                    JsonFileOperations.USERS_FOLDER,
                    user.getUserName()
            ));

            data.userLogin(
                    user.getUserName(),
                    user.getPass(),
                    Mockito.mock(OutputAgent.class),
                    Mockito.mock(InputAgent.class)
            );

            Assertions.assertTrue(data.isUserOnline(user.getUserName()));
        }
    }

    @Test
    void checkingIfAnOfflineUserIsOnline_ShouldReturnFalse() {
        Assertions.assertFalse(data.isUserOnline("Viggo"));
    }

    @Test
    void makingFriends_ShouldResultInBothUsersAddingTheOtherToTheirFriendListAndStoringTheUsers() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            var bob = new User(
                    "bob",
                    "1234"
            );
            var alice = new User(
                    "alice",
                    "1234"
            );

            data.makeFriends(
                    bob,
                    alice
            );

            mockJsonFileOperations.verify(
                    () -> JsonFileOperations.storeObjectToFile(
                            bob,
                            JsonFileOperations.USERS_FOLDER,
                            bob.getUserName()
                    ),
                    times(1)
            );

            mockJsonFileOperations.verify(
                    () -> JsonFileOperations.storeObjectToFile(
                            alice,
                            JsonFileOperations.USERS_FOLDER,
                            alice.getUserName()
                    ),
                    times(1)
            );

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            1,
                            bob
                                    .getFriends()
                                    .size()
                    ),
                    () -> Assertions.assertEquals(
                            1,
                            alice
                                    .getFriends()
                                    .size()
                    ),
                    () -> Assertions.assertEquals(
                            bob.getUserName(),
                            alice
                                    .getFriends()
                                    .getFirst()
                    ),
                    () -> Assertions.assertEquals(
                            alice.getUserName(),
                            bob
                                    .getFriends()
                                    .getFirst()
                    )
            );
        }
    }

    @Test
    void getUserNames_ShouldGiveAListOfUserNamesOfExistingUsers() {
        try (var mockJsonFileOperations = Mockito.mockStatic(JsonFileOperations.class)) {
            var users = List.of(
                    new User(
                            "bob",
                            "1234"
                    ),
                    new User(
                            "alice",
                            "1234"
                    )
            );

            mockJsonFileOperations
                    .when(() -> JsonFileOperations.loadAllObjectsInFolder(
                            JsonFileOperations.USERS_FOLDER,
                            User.class
                    ))
                    .thenReturn(users);

            data.loadUsers();

            var usernames = data.getUserNames();

            Assertions.assertEquals(
                    List.of(
                            "bob",
                            "alice"
                    ),
                    usernames
            );
        }
    }
}
