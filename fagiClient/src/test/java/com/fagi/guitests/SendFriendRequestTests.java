package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.model.FriendRequest;
import com.fagi.model.SearchUsersResult;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import com.fagi.threads.ThreadPool;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
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
import org.testfx.matcher.control.LabeledMatchers;

import java.util.ArrayList;

import static com.fagi.helpers.WaitForFXEventsTestHelper.addIngoingMessageToInputHandler;

@ExtendWith(ApplicationExtension.class)
public class SendFriendRequestTests {
    private Communication communication;
    private InputHandler inputHandler;
    private final ThreadPool threadPool = new ThreadPool();

    @BeforeAll
    static void initialize() {
        System.out.println("Starting send friend request tests");
        FxAssert.assertContext().setNodeFinder(new FagiNodeFinderImpl(FxService.serviceContext().getWindowFinder()));
    }

    @Test
    void WhenClickingOnSearchContactThatIsNotAFriend_ANewFriendInvitationIsOpened(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueSearchContact");
        FxAssert.verifyThat(
                "#InvitationConversation",
                NodeMatchers.isNotNull()
        );
    }

    @Test
    void WhenOpeningANewFriendInvitation_SendInvitationContainsContactUserName(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()), 2);
        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueSearchContact");
        Assumptions.assumeTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());

        FxAssert.verifyThat(
                "#name",
                LabeledMatchers.hasText(username));
    }

    @Test
    void WhenClickingOnSendInvitationButton_FriendRequestIsSendToServer(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueSearchContact");
        Assumptions.assumeTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());

        WaitForFXEventsTestHelper.clickOn(robot, "#send");

        var argument = ArgumentCaptor.forClass(FriendRequest.class);
        Mockito.verify(communication, Mockito.times(3)).sendObject(argument.capture());

        Assertions.assertEquals(argument.getAllValues().get(2).getFriendUsername(), username);
    }

    @Test
    void WhenClickingOnSendInvitationButton_TheWrittenMessageIsIncluded(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        var message = "Do you want to build a snowman?";
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        WaitForFXEventsTestHelper.clickOn(robot, "#UniqueSearchContact");
        Assumptions.assumeTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#message", message);
        WaitForFXEventsTestHelper.clickOn(robot, "#send");

        var argument = ArgumentCaptor.forClass(FriendRequest.class);
        Mockito.verify(communication, Mockito.times(3)).sendObject(argument.capture());

        Assertions.assertEquals(argument.getAllValues().get(2).getMessage().getData(), message);
    }

    @Start
    void start(Stage stage) {
        var fagiApp = Mockito.mock(FagiApp.class);
        var draggable = new Draggable(stage);

        communication = Mockito.mock(Communication.class);
        inputHandler = Mockito.mock(InputHandler.class);

        Mockito.doCallRealMethod().when(communication).setInputHandler(inputHandler);
        Mockito.doCallRealMethod().when(communication).getInputDistributor();
        Mockito.doCallRealMethod().when(inputHandler).setupDistributor(Mockito.any());
        Mockito.doCallRealMethod().when(inputHandler).addIngoingMessage(Mockito.any());
        Mockito.doCallRealMethod().when(inputHandler).getDistributor();
        Mockito.doAnswer(invocationOnMock -> new MasterLogin(fagiApp, communication, stage, draggable))
                .when(fagiApp).showLoginScreen();

        var comspy = Mockito.spy(communication);
        Mockito.doNothing().when(comspy).sendObject(Mockito.any());

        var inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor(threadPool);

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        MainScreen mainScreen = new MainScreen("Test", communication, stage);
        mainScreen.initCommunication(threadPool);
        stage.setScene(new Scene(mainScreen));
        stage.show();
    }
}
