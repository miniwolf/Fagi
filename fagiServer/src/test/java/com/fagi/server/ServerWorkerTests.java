package com.fagi.server;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.encryption.AES;
import com.fagi.encryption.AESKey;
import com.fagi.encryption.Conversion;
import com.fagi.model.Login;
import com.fagi.model.Session;
import com.fagi.model.User;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.util.DataTestUtil;
import com.fagi.util.RunOnceStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

class ServerWorkerTests extends ServerTests {
    @Test
    void givenServerSocketAcceptsConnection_ThenShouldCreateInputAndOutputWorkerThreadsSetToBeDaemons() throws IOException {
        AES aes = new AES();
        var inputStream = setupInputStream(List.of(new Session((AESKey) aes.getKey())));

        var outputStream = new ByteArrayOutputStream();

        var serverSocket = setupServerSocketMock(
                outputStream,
                inputStream
        );

        var server = new Server(
                serverPort,
                data
        );
        var runStrategy = new RunOnceStrategy();
        server.setIsRunningStrategy(() -> {
            var isRunning = runStrategy.isRunning();
            if (!isRunning) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return isRunning;
        });

        server.start(serverSocket);

        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        1,
                        server
                                .getInputWorkerThreads()
                                .size()
                ),
                () -> Assertions.assertTrue(server
                                                    .getInputWorkerThreads()
                                                    .getFirst()
                                                    .isDaemon()),
                () -> Assertions.assertEquals(
                        1,
                        server
                                .getOutputWorkerThreads()
                                .size()
                ),
                () -> Assertions.assertTrue(server
                                                    .getOutputWorkerThreads()
                                                    .getFirst()
                                                    .isDaemon())
        );
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void givenServerSocketAcceptsConnection_ThenShouldProcessInputFromInputStreamAndSendResponse() throws IOException {
        AES aes = new AES();

        var encryptedResponse = aes.encrypt(Conversion.convertToBytes(new AllIsWell()));

        var inputStream = setupInputStream(List.of(new Session((AESKey) aes.getKey())));

        var outputStream = new ByteArrayOutputStream();

        var serverSocket = setupServerSocketMock(
                outputStream,
                inputStream
        );

        var server = new Server(
                serverPort,
                data
        );
        var runStrategy = new RunOnceStrategy();
        server.setIsRunningStrategy(() -> {
            var isRunning = runStrategy.isRunning();
            if (!isRunning) {
                // Wait for output stream to contain response
                while (outputStream.size() < encryptedResponse.length) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return isRunning;
        });

        server.start(serverSocket);

        Assertions.assertAll(
                () -> Assertions.assertTrue(outputStream.toByteArray().length > 0),
                () -> {
                    ByteArrayInputStream sentDataInputStream = new ByteArrayInputStream(outputStream.toByteArray());
                    ObjectInputStream sentObjIn = new ObjectInputStream(sentDataInputStream);

                    var o = Conversion.convertFromBytes(aes.decrypt((byte[]) sentObjIn.readObject()));
                    Assertions.assertInstanceOf(
                            AllIsWell.class,
                            o
                    );
                }
        );
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void givenUserSendingTextMessage_WhenServerIsRunning_ThenConversationShouldContainMessage() throws IOException, ClassNotFoundException {
        var user = new User(
                "bob",
                "1234"
        );
        var conversation = new Conversation(
                42,
                "Test Conversation",
                ConversationType.Single
        );
        conversation.addUser(user.getUserName());
        var aes = new AES();

        var encryptedResponse = aes.encrypt(Conversion.convertToBytes(new AllIsWell()));

        Mockito
                .when(data.getUser(user.getUserName()))
                .thenReturn(user);
        Mockito
                .when(data.getConversation(conversation.getId()))
                .thenReturn(conversation);
        Mockito
                .when(data.userLogin(
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any()
                ))
                .thenReturn(new AllIsWell());

        TextMessage testMessage = new TextMessage(
                "Test message",
                user.getUserName(),
                conversation.getId()
        );
        var inputStream = setupInputStream(List.of(
                new Session((AESKey) aes.getKey()),
                new Login(
                        user.getUserName(),
                        user.getPass()
                ),
                testMessage
        ));

        var outputStream = new ByteArrayOutputStream();

        var serverSocket = setupServerSocketMock(
                outputStream,
                inputStream
        );

        var server = new Server(
                serverPort,
                data
        );

        var runStrategy = new RunOnceStrategy();
        server.setIsRunningStrategy(() -> {
            var isRunning = runStrategy.isRunning();
            if (!isRunning) {
                // Wait for output stream to contain response
                while (outputStream.size() < encryptedResponse.length * 3) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return isRunning;
        });

        server.start(serverSocket);

        ByteArrayInputStream sentDataInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ObjectInputStream sentObjIn = new ObjectInputStream(sentDataInputStream);

        var response1 = Conversion.convertFromBytes(aes.decrypt((byte[]) sentObjIn.readObject()));
        var response2 = Conversion.convertFromBytes(aes.decrypt((byte[]) sentObjIn.readObject()));
        var response3 = Conversion.convertFromBytes(aes.decrypt((byte[]) sentObjIn.readObject()));

        ArgumentCaptor<Conversation> argumentCaptor = DataTestUtil.verifyStoreConversationCalled(
                data,
                1
        );
        Conversation capturedConversation = argumentCaptor.getValue();

        Assertions.assertAll(
                () -> Assertions.assertNotNull(server.getHandler()),
                () -> Assertions.assertInstanceOf(
                        AllIsWell.class,
                        response1
                ),
                () -> Assertions.assertInstanceOf(
                        AllIsWell.class,
                        response2
                ),
                () -> Assertions.assertInstanceOf(
                        AllIsWell.class,
                        response3
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
                        testMessage,
                        capturedConversation
                                .getMessages()
                                .getFirst()
                )
        );
    }

    private static ByteArrayInputStream setupInputStream(List<Object> objectsToSend) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        for (var o : objectsToSend) {
            objectOutputStream.writeObject(o);
        }
        objectOutputStream.flush();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private static ServerSocket setupServerSocketMock(
            ByteArrayOutputStream outputStream,
            ByteArrayInputStream inputStream) throws IOException {
        var serverSocket = Mockito.mock(ServerSocket.class);
        var socket = Mockito.mock(Socket.class);

        Mockito
                .when(serverSocket.accept())
                .thenReturn(socket);
        Mockito
                .when(socket.getOutputStream())
                .thenReturn(outputStream);
        Mockito
                .when(socket.getInputStream())
                .thenReturn(inputStream);
        return serverSocket;
    }
}
