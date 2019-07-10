package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendRequestList;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hamcrest.collection.IsEmptyIterable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.NodeMatchers;

import java.util.ArrayList;

import static com.fagi.helpers.WaitForFXEventsTestHelper.addIngoingMessageToInputHandler;

@ExtendWith(ApplicationExtension.class)
public class ReceiveFriendRequestTests {
    private static final String myUsername = "Test";
    private Communication communication;
    private InputHandler inputHandler;

    @Test
    void ReceivedRequestAppearsInChatHistory(FxRobot robot) {
        var username = "a";
        var message = "Be my friend";
        var requests = new ArrayList<FriendRequest>();
        requests.add(new FriendRequest(myUsername, new TextMessage(message, username, 0)));
        addIngoingMessageToInputHandler(inputHandler, new FriendRequestList(new DefaultListAccess<>(requests)));

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");

        FxAssert.verifyThat(
                "#UniqueInviteItem",
                NodeMatchers.isNotNull(),
                builder -> builder.append("Received request should be in chat history."));
    }

    @Test
    void WindowOpensWhenClickingOnReceivedInvitation(FxRobot robot) {
        var username = "a";
        var message = "Be my friend";
        var requests = new ArrayList<FriendRequest>();
        requests.add(new FriendRequest(myUsername, new TextMessage(message, username, 0)));
        addIngoingMessageToInputHandler(inputHandler, new FriendRequestList(new DefaultListAccess<>(requests)));

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");
        WaitForFXEventsTestHelper.clickOn(robot, ".inviteItem");

        FxAssert.verifyThatIter(
                "#InvitationConversation",
                IsEmptyIterable.emptyIterableOf(Node.class),
                builder -> builder.append("Clicking on invitation should open invitation window."));
    }

    @Start
    void start(Stage stage) {
        var fagiApp = Mockito.mock(FagiApp.class);
        var draggable = new Draggable(stage);

        communication = Mockito.mock(Communication.class);
        inputHandler = Mockito.mock(InputHandler.class);

        Mockito.doCallRealMethod().when(communication).setInputHandler(inputHandler);
        Mockito.doCallRealMethod().when(inputHandler).setupDistributor();
        Mockito.doCallRealMethod().when(inputHandler).addIngoingMessage(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> new MasterLogin(fagiApp, communication, stage, draggable))
                .when(fagiApp).showLoginScreen();

        var comspy = Mockito.spy(communication);
        Mockito.doNothing().when(comspy).sendObject(Mockito.any());

        var inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor();

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        MainScreen mainScreen = new MainScreen(myUsername, communication, stage);
        mainScreen.initCommunication();
        stage.setScene(new Scene(mainScreen));
        stage.show();
    }
}
