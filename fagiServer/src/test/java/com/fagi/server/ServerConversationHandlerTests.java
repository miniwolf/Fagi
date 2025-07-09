package com.fagi.server;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.model.Data;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.util.DataTestUtil;
import com.fagi.utility.JsonFileOperations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;

class ServerConversationHandlerTests extends ServerTests {
    @Test
    void whenServerIsStarted_ThenConversationHandlerThreadIsStartedAsDaemon() throws InterruptedException {
        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);
        server.start(null);
        Thread.sleep(100);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(server.getConversationHandlerThread()),
                () -> Assertions.assertTrue(server
                                                    .getConversationHandlerThread()
                                                    .isDaemon())
        );
    }

    @Test
    void whenServerIsShuttingDown_ThenConversationHandlerThreadShouldBeInterrupted() {
        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);
        server.start(null);

        Assertions.assertTrue(server
                                      .getConversationHandlerThread()
                                      .isInterrupted());
    }

    @Test
    void givenConversationHandlerHasMessage_WhenServerIsRunning_ThenConversationShouldBeStoredWithNewMessage() throws InterruptedException {
        var conversation = new Conversation(
                42,
                "test convo",
                ConversationType.Single
        );
        conversation.addUser("bob");
        conversation.addUser("eva");

        Mockito
                .when(data.getConversation(42))
                .thenReturn(conversation);
        Mockito
                .when(data.isUserOnline(Mockito.any()))
                .thenReturn(false);

        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);
        TextMessage evaMessage = new TextMessage(
                "Hello, friend.",
                "Eva",
                42
        );
        server
                .getHandler()
                .addMessage(evaMessage);
        server.setIsRunningStrategy(() -> {
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } while (server
                    .getHandler()
                    .queueSize() > 0);
            return false;
        });

        server.start(null);

        ArgumentCaptor<Conversation> argumentCaptor = DataTestUtil.verifyStoreConversationCalled(
                data,
                1
        );
        Conversation capturedConversation = argumentCaptor.getValue();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(server.getHandler()),
                () -> Assertions.assertEquals(
                        0,
                        server
                                .getHandler()
                                .queueSize()
                ),
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
                        evaMessage,
                        capturedConversation
                                .getMessages()
                                .getFirst()
                )
        );
    }

    @Test
    void givenConversationFileExists_WhenServerIsStarting_ThenConversationsShouldBeLoadedIntoData() {
        deleteConversationsFolder();

        var conversation = new Conversation(
                13,
                "Test Conversation",
                ConversationType.Single
        );
        conversation.addUser("bob");
        conversation.addUser("eva");
        var data = new Data();

        JsonFileOperations.storeConversation(conversation);

        var server = new Server(
                serverPort,
                data
        );
        server.setRunning(false);

        server.start(null);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        conversation.getId() + 1,
                        data.getNextConversationId()
                ),
                () -> Assertions.assertEquals(
                        data.getConversation(13),
                        conversation
                )
        );

        deleteConversationsFolder();
    }

    private void deleteConversationsFolder() {
        var conversationsFolder = new File(JsonFileOperations.CONVERSATION_FOLDER_PATH);

        if (conversationsFolder.exists()) {
            conversationsFolder.delete();
        }
    }
}
