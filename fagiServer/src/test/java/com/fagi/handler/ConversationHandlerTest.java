package com.fagi.handler;

import com.fagi.BaseFagiTest;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.logging.TestLogLevel;
import com.fagi.logging.TestLogRecord;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.util.DataTestUtil;
import com.fagi.worker.OutputAgent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.concurrent.TimeUnit;

class ConversationHandlerTest extends BaseFagiTest {
    private Data data;
    private ConversationHandler conversationHandler;

    @BeforeEach
    void setUp() {
        data = Mockito.mock(Data.class);
        conversationHandler = new ConversationHandler(data);
    }

    @Test
    void givenMessageAddedToConversationHandler_ThenQueueSizeShouldBeOne() {
        conversationHandler.addMessage(new TextMessage(
                "Test message",
                "bob",
                42
        ));

        Assertions.assertEquals(
                1,
                conversationHandler.queueSize()
        );
    }

    @Test
    void givenMessageInQueue_WhenRunningTick_ThenQueueSizeShouldBeZeroAfter() {
        var conversation = new Conversation(
                42,
                "Test conversation",
                ConversationType.Single
        );

        Mockito
                .when(data.getConversation(conversation.getId()))
                .thenReturn(conversation);

        conversationHandler.addMessage(new TextMessage(
                "Test message",
                "bob",
                conversation.getId()
        ));

        conversationHandler.tick();

        Assertions.assertEquals(
                0,
                conversationHandler.queueSize()
        );
    }

    @Test
    void givenMessageInQueue_WhenRunningTick_ThenShouldStoreConversationWithMessage() {
        var conversation = new Conversation(
                42,
                "Test conversation",
                ConversationType.Single
        );

        Mockito
                .when(data.getConversation(conversation.getId()))
                .thenReturn(conversation);

        TextMessage message = new TextMessage(
                "Test message",
                "bob",
                conversation.getId()
        );
        conversationHandler.addMessage(message);

        conversationHandler.tick();

        ArgumentCaptor<Conversation> conversationArgumentCaptor = DataTestUtil.verifyStoreConversationCalled(
                data,
                1
        );
        Conversation capturedConversation = conversationArgumentCaptor.getValue();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        conversation.getId(),
                        capturedConversation.getId()
                ),
                () -> Assertions.assertEquals(
                        1,
                        capturedConversation
                                .getMessages()
                                .size()
                ),
                () -> Assertions.assertEquals(
                        message,
                        capturedConversation
                                .getMessages()
                                .getFirst()
                )
        );
    }

    @Test
    void givenMessageInQueue_WhenTwoOfThreeParticipantsAreOnline_ThenOnlyTheOnlineParticipantsShouldReceiveTheMessage() {
        var user1 = new User(
                "bob",
                "1234"
        );
        var outputAgent1 = Mockito.mock(OutputAgent.class);
        var user2 = new User(
                "eva",
                "1234"
        );
        var outputAgent2 = Mockito.mock(OutputAgent.class);
        var user3 = new User(
                "alice",
                "1234"
        );
        var outputAgent3 = Mockito.mock(OutputAgent.class);

        var conversation = new Conversation(
                42,
                "Test Conversation",
                ConversationType.Single
        );
        conversation.addUser(user1.getUserName());
        conversation.addUser(user2.getUserName());
        conversation.addUser(user3.getUserName());

        Mockito
                .when(data.getConversation(conversation.getId()))
                .thenReturn(conversation);

        Mockito
                .when(data.isUserOnline(user1.getUserName()))
                .thenReturn(false);
        Mockito
                .when(data.isUserOnline(user2.getUserName()))
                .thenReturn(true);
        Mockito
                .when(data.isUserOnline(user3.getUserName()))
                .thenReturn(true);

        Mockito
                .when(data.getOutputAgent(user1.getUserName()))
                .thenReturn(outputAgent1);
        Mockito
                .when(data.getOutputAgent(user2.getUserName()))
                .thenReturn(outputAgent2);
        Mockito
                .when(data.getOutputAgent(user3.getUserName()))
                .thenReturn(outputAgent3);

        var message = new TextMessage(
                "Test message",
                user2.getUserName(),
                conversation.getId()
        );

        conversationHandler.addMessage(message);

        conversationHandler.tick();

        Mockito
                .verify(
                        outputAgent1,
                        Mockito.times(0)
                )
                .addMessage(Mockito.any());
        Mockito
                .verify(
                        outputAgent2,
                        Mockito.times(1)
                )
                .addMessage(Mockito.eq(message));
        Mockito
                .verify(
                        outputAgent3,
                        Mockito.times(1)
                )
                .addMessage(Mockito.eq(message));
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testConversationHandlerLogsMessageWhenInterrupted() throws InterruptedException {
        var thread = new Thread(conversationHandler);
        thread.setDaemon(true);

        thread.start();

        Thread.sleep(100);

        thread.interrupt();

        List<TestLogRecord<?>> logRecords = lookupLogRecordsForClass(ConversationHandler.class);

        while (logRecords.isEmpty()) {
            Thread.sleep(100);
            logRecords = lookupLogRecordsForClass(ConversationHandler.class);
        }

        Assertions.assertEquals(
                1,
                logRecords.size()
        );

        TestLogRecord<?> logRecord = logRecords.getFirst();

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "Interrupted ConversationHandler thread.",
                        logRecord.message()
                ),
                () -> Assertions.assertEquals(
                        TestLogLevel.DEBUG,
                        logRecord.logLevel()
                ),
                () -> Assertions.assertInstanceOf(
                        InterruptedException.class,
                        logRecord.throwable()
                ),
                () -> Assertions.assertTrue(thread.isInterrupted())
        );
    }
}