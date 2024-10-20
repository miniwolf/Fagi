package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.model.Friend;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import com.fagi.threads.ThreadPool;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hamcrest.collection.IsEmptyIterable;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxService;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(ApplicationExtension.class)
public class ConversationTests {

    private InputHandler inputHandler;
    private Communication communication;
    private final ThreadPool threadPool = new ThreadPool();

    @BeforeAll
    public static void intialize() {
        System.out.println("Starting conversation tests");
        FxAssert
                .assertContext()
                .setNodeFinder(new FagiNodeFinderImpl(FxService
                                                              .serviceContext()
                                                              .getWindowFinder()));
    }

    @AfterEach
    void dispose() {
        deleteFolder("MyUsername/conversations");
        deleteFolder("MyUsername");
        deleteFolder("conversations");
        threadPool.stopThreads();
    }

    private void deleteFolder(String path) {
        File conversationFolder = new File(path);
        if (conversationFolder.exists()) {
            for (String s : conversationFolder.list()) {
                new File(conversationFolder.getPath(), s).delete();
            }
            conversationFolder.delete();
        }
    }

    @Test
    void NoConversations_FriendsAreNotShownInChatList(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");

        FxAssert.verifyThatIter("#UniqueConversationItem",
                                IsEmptyIterable.emptyIterableOf(Node.class),
                                builder -> builder.append(
                                        "Conversation list should not contain any conversation elements.")
        );
    }

    @Test
    void ClickingContact_WillOpenConversation(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueContact");

        FxAssert.verifyThat("#UniqueConversation",
                            NodeMatchers.isNotNull(),
                            builder -> builder.append("Conversation should open when clicking on the contact item.")
        );
    }

    @Test
    void ClickingConversationItem_WillOpenConversation(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");

        FxAssert.verifyThat("#UniqueConversation",
                            NodeMatchers.isNotNull(),
                            builder -> builder.append("Conversation should open when clicking on the contact item.")
        );
    }

    @Test
    void ClickingContact_WillOnlyOpenOneConversation(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueContact");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueContact");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueContact");

        FxAssert.verifyThatIter("#UniqueConversation",
                                IsIterableWithSize.iterableWithSize(1),
                                builder -> builder.append("Only one conversation when clicking on the contact item.")
        );
    }

    @Test
    void ClickingConversationItem_WillOnlyOpenOneConversation(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");

        FxAssert.verifyThatIter("#UniqueConversation",
                                IsIterableWithSize.iterableWithSize(1),
                                builder -> builder.append("Only one conversation when clicking on the contact item.")
        );
    }

    @Test
    void PressingEnterInConversationField_WillSendMessageToServer(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");

        robot
                .clickOn("#conversationTextarea")
                .write("Hello")
                .push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        var textMessageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        Mockito
                .verify(communication, Mockito.times(1))
                .sendObject(textMessageCaptor.capture());

        var message = textMessageCaptor
                .getValue();
        Assertions.assertEquals("Hello\n", message.data(), "Message should be send to server");
    }

    @Test
    void SendMessageFromConversation_ClearsMessageField(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");

        robot
                .clickOn("#conversationTextarea")
                .write("Hello")
                .push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        FxAssert.verifyThat("#conversationTextarea",
                            TextInputControlMatchers.hasText(""),
                            builder -> builder.append("Field should be emptied after sending the message")
        );
    }

    @Test
    void PressingShiftEnter_WillInsertNewLine(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");

        robot
                .clickOn("#conversationTextarea")
                .write("Hello")
                .push(KeyCode.SHIFT, KeyCode.ENTER)
                .write("Dimmer");
        WaitForAsyncUtils.waitForFxEvents();

        FxAssert.verifyThat("#conversationTextarea",
                            TextInputControlMatchers.hasText("Hello\nDimmer"),
                            builder -> builder.append("Field should add new line with shift enter.")
        );
    }

    @Test
    void VerifyTextMessageFormat(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueConversationItem");

        robot
                .clickOn("#conversationTextarea")
                .write("Hello")
                .push(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        var textMessageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        Mockito
                .verify(communication, Mockito.times(1))
                .sendObject(textMessageCaptor.capture());

        var message = textMessageCaptor
                .getValue();
        Assertions.assertAll(() -> Assertions.assertEquals("Hello\n",
                                                           message.data(),
                                                           "Message should be send to server"
                             ),
                             () -> Assertions.assertEquals("MyUsername",
                                                           message
                                                                   .getMessageInfo()
                                                                   .getSender(),
                                                           "Wrong sender"
                             ),
                             () -> Assertions.assertEquals(1,
                                                           message
                                                                   .getMessageInfo()
                                                                   .getConversationID(),
                                                           "Wrong conversation ID"
                             )
        );
    }

    @Test
    void MessageFromOtherUser_WillBeDisplayed(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");
        var textMessage = new TextMessage("Hello there.", "test", 1);

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, textMessage);

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueContact");

        FxAssert.verifyThat("#UniqueTheirMessage",
                            NodeMatchers.isNotNull(),
                            builder -> builder.append("The message should be present as a their message")
        );
    }

    @Test
    void MessageFromUser_WillBeDisplayed(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
        }};
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser("Friend");
        var textMessage = new TextMessage("Hello there.", "MyUsername", 1);

        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(
                inputHandler,
                new FriendList(new DefaultListAccess<>(friends))
        );
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, conversation);
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, textMessage);

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueContact");

        FxAssert.verifyThat("#UniqueMyMessage",
                            NodeMatchers.isNotNull(),
                            builder -> builder.append("The message should be present as a their message")
        );
    }

    @Start
    public void start(Stage stage) {
        var fagiApp = Mockito.mock(FagiApp.class);
        var draggable = new Draggable(stage);

        communication = Mockito.spy(Communication.class);
        inputHandler = Mockito.mock(InputHandler.class);

        Mockito
                .doCallRealMethod()
                .when(communication)
                .setInputHandler(inputHandler);
        Mockito
                .doCallRealMethod()
                .when(communication)
                .getInputDistributor();
        Mockito
                .doCallRealMethod()
                .when(communication)
                .close();
        Mockito
                .doCallRealMethod()
                .when(inputHandler)
                .setupDistributor(Mockito.any());
        Mockito
                .doCallRealMethod()
                .when(inputHandler)
                .getDistributor();
        Mockito
                .doCallRealMethod()
                .when(inputHandler)
                .addIngoingMessage(Mockito.any());
        Mockito
                .doCallRealMethod()
                .when(inputHandler)
                .close();
        Mockito
                .doAnswer(invocationOnMock -> new MasterLogin(fagiApp, communication, stage, draggable))
                .when(fagiApp)
                .showLoginScreen();
        Mockito
                .doNothing()
                .when(communication)
                .sendObject(Mockito.any());

        threadPool.startThread(inputHandler, "InputHandler - ConversationTests");

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor(threadPool);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        var screen = new MainScreen("MyUsername", communication, stage);
        screen.initCommunication(threadPool);
        stage.setScene(new Scene(screen));
        stage.show();
    }
}
