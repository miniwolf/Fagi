package com.fagi.worker;

import com.fagi.encryption.AES;
import com.fagi.encryption.Conversion;
import com.fagi.model.Data;
import com.fagi.model.FriendRequest;
import com.fagi.model.User;
import com.fagi.model.UserLoggedIn;
import com.fagi.model.UserLoggedOut;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserOnline;
import com.fagi.util.NeverRunStrategy;
import com.fagi.util.RunOnceStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class OutputWorkerTest {
    private final ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
    private ObjectOutputStream objOut;
    private Data data;
    private OutputWorker outputWorker;

    @BeforeEach
    void setUp() {
        objOut = Mockito.mock(ObjectOutputStream.class);
        data = Mockito.mock(Data.class);
        var aes = Mockito.mock(AES.class);
        when(aes.encrypt(any())).thenAnswer(i -> i.getArgument(0));

        outputWorker = new OutputWorker(
                objOut,
                data
        );
        outputWorker.setAes(aes);
    }

    @Test
    void givenOutputWorkerCreated_ThenShouldBeRunning() {
        Assertions.assertTrue(outputWorker.isRunning());
    }

    @Nested
    class OutputWorkerErrorHandlingTests {
        @AfterEach
        void tearDown() {
            System.setErr(System.err);
            System.setOut(System.out);
        }

        @Test
        void givenWritingObjectGivesIOException_WhenRunningIsFalse_ThenShouldNotWriteToConsole() throws IOException {
            var outContent = new ByteArrayOutputStream();
            var errorContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errorContent));
            System.setOut(new PrintStream(outContent));

            doThrow(new IOException())
                    .when(objOut)
                    .writeObject(any());
            outputWorker.setIsRunningStrategy(new NeverRunStrategy());
            outputWorker.addResponse("dummy");

            outputWorker.run();

            Assertions.assertAll(
                    () -> Assertions.assertFalse(outContent
                                                         .toString()
                                                         .contains("java.io.IOException")),
                    () -> Assertions.assertFalse(errorContent
                                                         .toString()
                                                         .contains("java.io.IOException"))
            );
        }

        @Test
        void givenWritingObjectsGivesException_WhenRunningIsTrue_ThenRunningShouldBeSetToFalse() throws IOException {
            doThrow(new IOException())
                    .when(objOut)
                    .writeObject(any());
            outputWorker.addResponse("dummy");

            outputWorker.run();

            Assertions.assertFalse(outputWorker.isRunning());
        }

        @Test
        void givenWritingObjectsGivesIOException_WhenRunningIsTrue_ThenShouldWriteToConsoleAsInfo() throws IOException {
            var outContent = new ByteArrayOutputStream();
            var errorContent = new ByteArrayOutputStream();
            System.setErr(new PrintStream(errorContent));
            System.setOut(new PrintStream(outContent));

            doThrow(new IOException())
                    .when(objOut)
                    .writeObject(any());
            outputWorker.addResponse("dummy");

            outputWorker.run();

            Assertions.assertAll(
                    () -> Assertions.assertTrue(outContent
                                                        .toString()
                                                        .contains("java.io.IOException")),
                    () -> Assertions.assertFalse(errorContent
                                                         .toString()
                                                         .contains("java.io.IOException"))
            );
        }

        @Test
        void givenWritingObjectsGivesException_WhenRunningIsTrue_ThenShouldLogoutUser() throws IOException {
            var outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            doThrow(new IOException())
                    .when(objOut)
                    .writeObject(any());
            outputWorker.addResponse("dummy");
            outputWorker.setUserName("bob");

            outputWorker.run();

            Mockito.verify(data, times(1)).userLogout("bob");

            Assertions.assertTrue(outContent.toString().contains("Logging out user bob"));
        }
    }

    @Nested
    class OutputWorkerEqualListsTests {
        @Test
        void givenBothListsAreNull_WhenCallingEqualLists_ThenShouldReturnTrue() {
            Assertions.assertTrue(outputWorker.equalLists(
                    null,
                    null
            ));
        }

        @Test
        void givenFirstListIsNull_WhenCallingEqualLists_ThenShouldReturnFalse() {
            var list = List.of(
                    "foo",
                    "bar"
            );
            Assertions.assertFalse(outputWorker.equalLists(
                    null,
                    list
            ));
        }

        @Test
        void givenSecondListIsNull_WhenCallingEqualLists_ThenShouldReturnFalse() {
            var list = List.of(
                    "foo",
                    "bar"
            );
            Assertions.assertFalse(outputWorker.equalLists(
                    list,
                    null
            ));
        }

        @Test
        void givenTwoListsHaveDifferentSizes_WhenCallingEqualLists_ThenShouldReturnFalse() {
            var list1 = List.of("foo");
            var list2 = List.of(
                    "Very",
                    "bar"
            );
            Assertions.assertFalse(outputWorker.equalLists(
                    list1,
                    list2
            ));
        }

        @Test
        void givenTwoListsHaveSameContent_WhenCallingEqualLists_ThenShouldReturnTrue() {
            var list1 = List.of(
                    "foo",
                    "Very",
                    "bar"
            );
            var list2 = List.of(
                    "Very",
                    "bar",
                    "foo"
            );
            Assertions.assertTrue(outputWorker.equalLists(
                    list1,
                    list2
            ));
        }

        @Test
        void givenTwoListsHaveSameContentInDifferentOrder_WhenCallingEqualLists_ThenShouldReturnTrue() {
            var list1 = List.of(
                    "bar",
                    "foo",
                    "Very"
            );
            var list2 = List.of(
                    "bar",
                    "foo",
                    "Very"
            );
            Assertions.assertTrue(outputWorker.equalLists(
                    list1,
                    list2
            ));
        }

        @Test
        void givenTwoListsHaveDifferentContent_WhenCallingEqualLists_ThenShouldReturnTrue() {
            var list1 = List.of(
                    "bar",
                    "foo",
                    "Very"
            );
            var list2 = List.of(
                    "test",
                    "fisk",
                    "hands"
            );
            Assertions.assertFalse(outputWorker.equalLists(
                    list1,
                    list2
            ));
        }
    }

    @Nested
    class OutputWorkerSendFriendRequestListTests {
        @Test
        void givenNoUsernameInOutputWorker_WhenCheckingFriendRequestList_ThenShouldNotSendFriendRequestList() throws IOException {
            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(2));

            outputWorker.run();

            verifyObjectsHaveBeenSent(0);
        }

        @Test
        void givenMessageIsAddedToQueueAfterSendIncMessagesIsDone_WhenRunningIsTrue_ThenShouldNotSendFriendRequestList() throws IOException {
            var user = new User(
                    "bob",
                    "password"
            );
            user
                    .getFriendReq()
                    .add(new FriendRequest(
                            "Eva",
                            new TextMessage(
                                    "Please be my friend",
                                    "Eva",
                                    -1
                            )
                    ));
            var message = new UserLoggedOut("bob");

            Mockito
                    .when(data.getUser(user.getUserName()))
                    .thenReturn(user);
            Mockito
                    .doAnswer(createAnswerMockThatPerformsActionOnFirstInvocation(() -> outputWorker.addMessage(message)))
                    .when(objOut)
                    .reset();

            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(2));
            outputWorker.setUserName(user.getUserName());

            outputWorker.run();

            verifyObjectsHaveBeenSent(1);
            Mockito
                    .verify(
                            data,
                            never()
                    )
                    .getUser(user.getUserName());

            byte[] messageObject = captor.getValue();

            Assertions.assertArrayEquals(
                    Conversion.convertToBytes(message),
                    messageObject
            );
        }

        @Test
        void givenResponseIsAddedToQueueAfterSendIncResponsesIsDone_WhenRunningIsTrue_ThenShouldNotSendFriendRequestList() throws IOException {
            var user = new User(
                    "bob",
                    "password"
            );
            user
                    .getFriendReq()
                    .add(new FriendRequest(
                            "Eva",
                            new TextMessage(
                                    "Please be my friend",
                                    "Eva",
                                    -1
                            )
                    ));
            var response = new AllIsWell();

            Mockito
                    .when(data.getUser(user.getUserName()))
                    .thenReturn(user);
            Mockito
                    .doAnswer(createAnswerMockThatPerformsActionOnFirstInvocation(() -> outputWorker.addResponse(new AllIsWell())))
                    .when(objOut)
                    .reset();

            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(2));
            outputWorker.setUserName(user.getUserName());

            outputWorker.run();

            verifyObjectsHaveBeenSent(1);
            Mockito
                    .verify(
                            data,
                            never()
                    )
                    .getUser(user.getUserName());

            byte[] responseObject = captor.getValue();

            Assertions.assertArrayEquals(
                    Conversion.convertToBytes(response),
                    responseObject
            );
        }

        @Test
        void givenUsernameIsSet_WhenCheckingFriendList_ThenShouldLookupUserFromData() {
            var user = new User(
                    "bob",
                    "password"
            );

            Mockito
                    .when(data.getUser(user.getUserName()))
                    .thenReturn(user);

            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(2));
            outputWorker.setUserName(user.getUserName());

            outputWorker.run();

            Mockito
                    .verify(
                            data,
                            times(1)
                    )
                    .getUser(user.getUserName());
        }

        @Test
        void givenUserHasNewFriendRequests_WhenCheckingFriendList_ThenShouldSendFriendRequestList() throws IOException {
            var user = new User(
                    "bob",
                    "password"
            );
            user
                    .getFriendReq()
                    .add(new FriendRequest(
                            "eva",
                            null
                    ));

            var expectedFriendRequestList = new FriendRequestList(new DefaultListAccess<>(user.getFriendReq()));

            Mockito
                    .when(data.getUser(user.getUserName()))
                    .thenReturn(user);

            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(2));
            outputWorker.setUserName(user.getUserName());

            outputWorker.run();

            verifyObjectsHaveBeenSent(1);

            byte[] sentBytes = captor.getValue();

            Assertions.assertArrayEquals(
                    Conversion.convertToBytes(expectedFriendRequestList),
                    sentBytes
            );
        }

        @Test
        void givenCheckingSameFriendRequestListTwice_WhenCheckingFriendList_ThenShouldSendFriendRequestListOnce() throws IOException {
            var user = new User(
                    "bob",
                    "password"
            );
            user
                    .getFriendReq()
                    .add(new FriendRequest(
                            "eva",
                            null
                    ));

            var expectedFriendRequestList = new FriendRequestList(new DefaultListAccess<>(user.getFriendReq()));

            Mockito
                    .when(data.getUser(user.getUserName()))
                    .thenReturn(user);

            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(4));
            outputWorker.setUserName(user.getUserName());

            outputWorker.run();

            verifyObjectsHaveBeenSent(1);

            byte[] sentBytes = captor.getValue();

            Assertions.assertArrayEquals(
                    Conversion.convertToBytes(expectedFriendRequestList),
                    sentBytes
            );
        }

        @Test
        void whenCheckingFriendRequestListTwentyTimes_ThenRunShouldTakeAtLeastTwoSeconds() {
            outputWorker.setIsRunningStrategy(new WorkerRunCalledNTimesStrategy(21));

            var startTime = System.currentTimeMillis();
            outputWorker.run();
            var endTime = System.currentTimeMillis();

            System.out.println(endTime - startTime);

            Assertions.assertTrue(2000 <= endTime - startTime);
        }
    }

    @Nested
    class OutputWorkerSendMessagesAndResponsesTests {
        @Test
        void givenMessagesQueueHasTwoMessages_WhenRunningIsTrue_ThenHaveSentTwoObject() throws IOException {
            var outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            var user1LoggedInMessage = new UserLoggedIn("bob");
            var user2LoggedInMessage = new UserLoggedIn("eve");

            outputWorker.setIsRunningStrategy(new RunOnceStrategy());

            outputWorker.addMessage(user1LoggedInMessage);
            outputWorker.addMessage(user2LoggedInMessage);

            outputWorker.run();

            verifyObjectsHaveBeenSent(2);

            Mockito
                    .verify(
                            objOut,
                            times(1)
                    )
                    .reset();

            List<byte[]> messageObjects = captor.getAllValues();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            2,
                            messageObjects.size()
                    ),
                    () -> Assertions.assertArrayEquals(
                            Conversion.convertToBytes(user1LoggedInMessage),
                            messageObjects.getFirst()
                    ),
                    () -> Assertions.assertTrue(outContent
                                                        .toString()
                                                        .contains(user1LoggedInMessage.toString())),
                    () -> Assertions.assertArrayEquals(
                            Conversion.convertToBytes(user2LoggedInMessage),
                            messageObjects.getLast()
                    ),
                    () -> Assertions.assertTrue(outContent
                                                        .toString()
                                                        .contains(user2LoggedInMessage.toString()))
            );
        }

        @Test
        void givenResponseIsAddedToQueueAfterSendIncResponsesIsDone_WhenRunningBecomesFalseAfterSendingInitialResponses_ThenShouldSendTwoResponses() throws IOException {
            var response = new UserOnline();

            outputWorker.addResponse(response);

            Mockito
                    .doAnswer(createAnswerMockThatPerformsActionOnFirstInvocation(() -> Assertions.assertEquals(
                            0,
                            outputWorker.getResponseObjectsQueueSize()
                    )))
                    .when(objOut)
                    .reset();

            outputWorker.setIsRunningStrategy(new RunOnceStrategy());

            outputWorker.run();

            verifyObjectsHaveBeenSent(1);

            byte[] responseObjects = captor.getValue();

            Assertions.assertArrayEquals(
                    Conversion.convertToBytes(response),
                    responseObjects
            );
        }

        @Test
        void whenRunningIsFalse_ThenShouldSendAllRespondObjects() throws IOException {
            outputWorker.setIsRunningStrategy(new NeverRunStrategy());
            String firstResponse = "some response";
            AllIsWell secondResponse = new AllIsWell();
            outputWorker.addResponse(firstResponse);
            outputWorker.addResponse(secondResponse);

            Assertions.assertEquals(
                    2,
                    outputWorker.getResponseObjectsQueueSize()
            );

            outputWorker.run();

            verifyObjectsHaveBeenSent(2);

            List<byte[]> responseObjects = captor.getAllValues();

            Assertions.assertAll(
                    () -> Assertions.assertEquals(
                            2,
                            responseObjects.size()
                    ),
                    () -> Assertions.assertArrayEquals(
                            Conversion.convertToBytes(firstResponse),
                            responseObjects.getFirst()
                    ),
                    () -> Assertions.assertArrayEquals(
                            Conversion.convertToBytes(secondResponse),
                            responseObjects.getLast()
                    ),
                    () -> Assertions.assertEquals(
                            0,
                            outputWorker.getResponseObjectsQueueSize()
                    )
            );
        }
    }

    @Nested
    class OutputWorkerLoggingTests {
        @AfterEach
        void tearDown() {
            System.setOut(System.out);
        }

        @Test
        void whenRunningIsFalse_ThenClosingOutputShouldBePrintedToConsole() {
            var outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            outputWorker.setIsRunningStrategy(new NeverRunStrategy());

            outputWorker.run();

            Assertions.assertTrue(outContent
                                          .toString()
                                          .contains("Closing output"));
        }

        @Test
        void whenRunningIsTrue_ThenRunningShouldBePrintedToConsole() {
            var outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            outputWorker.setIsRunningStrategy(new RunOnceStrategy());

            outputWorker.run();

            Assertions.assertTrue(outContent
                                          .toString()
                                          .contains("Running"));
        }

        @Test
        void givenMessagesQueueHasOneMessage_WhenRunningIsTrue_ThenHaveSentOneObject() throws IOException {
            var outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            var userLoggedInMessage = new UserLoggedIn("bob");

            outputWorker.setIsRunningStrategy(new RunOnceStrategy());

            outputWorker.addMessage(userLoggedInMessage);

            outputWorker.run();

            verifyObjectsHaveBeenSent(1);

            Mockito
                    .verify(
                            objOut,
                            times(1)
                    )
                    .reset();

            byte[] messageObject = captor.getValue();

            Assertions.assertAll(
                    () -> Assertions.assertArrayEquals(
                            Conversion.convertToBytes(userLoggedInMessage),
                            messageObject
                    ),
                    () -> Assertions.assertTrue(outContent
                                                        .toString()
                                                        .contains(userLoggedInMessage.toString()))
            );
        }
    }

    private void verifyObjectsHaveBeenSent(int numberOfObjectsToSend) throws IOException {
        Mockito
                .verify(
                        objOut,
                        times(numberOfObjectsToSend)
                )
                .writeObject(captor.capture());

        Mockito
                .verify(
                        objOut,
                        times(numberOfObjectsToSend)
                )
                .flush();
    }

    private Answer<Void> createAnswerMockThatPerformsActionOnFirstInvocation(Runnable action) {
        return new Answer<>() {
            private boolean hasAnswered = false;

            @Override
            public Void answer(InvocationOnMock invocationOnMock) {
                if (!hasAnswered) {
                    action.run();
                }
                hasAnswered = true;
                return null;
            }
        };
    }
}