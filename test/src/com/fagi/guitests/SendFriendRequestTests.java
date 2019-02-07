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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.Optional;

import static com.fagi.helpers.WaitForFXEventsTestHelper.addIngoingMessageToInputHandler;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;


@ExtendWith(ApplicationExtension.class)
public class SendFriendRequestTests {
    private Communication communication;
    private InputHandler inputHandler;

    @Test
    void WhenClickingOnSearchContactThatIsNotAFriend_ANewFriendInvitationIsOpened(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));
        Assume.assumeNotNull(robot.clickOn("#UniqueSearchContact"));
        Assert.assertTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());
    }

    @Test
    void WhenOpeningANewFriendInvitation_SendInvitationContainsContactUserName(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()), 2);
        Assume.assumeNotNull(robot.clickOn("#UniqueSearchContact"));
        Assume.assumeTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());
        Optional<Label> nameLabel = robot.lookup("#name").tryQuery();
        assertThat(nameLabel).hasValueSatisfying(label ->
                assertThat(label.getText()).isEqualTo(username));
    }

    @Test
    void WhenClickingOnSendInvitationButton_FriendRequestIsSendToServer(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));
        Assume.assumeNotNull(robot.clickOn("#UniqueSearchContact"));
        Assume.assumeTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());
        Assume.assumeTrue(robot.lookup("#send").tryQuery().isPresent());

        WaitForFXEventsTestHelper.clickOn(robot, "#send");

        var argument = ArgumentCaptor.forClass(FriendRequest.class);
        Mockito.verify(communication, Mockito.times(3)).sendObject(argument.capture());

        Assert.assertThat(argument.getAllValues().get(2).getFriendUsername(), is(username));
    }

    @Test
    void WhenClickingOnSendInvitationButton_TheWrittenMessageIsIncluded(FxRobot robot) {
        var username = "a";
        var usernames = new ArrayList<String>() {{ add(username); }};
        var message = "Do you want to build a snowman?";
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));
        Assume.assumeNotNull(robot.clickOn("#UniqueSearchContact"));
        Assume.assumeTrue(robot.lookup("#InvitationConversation").tryQuery().isPresent());

        Node messageField = robot.lookup("#message").tryQuery().orElse(null);
        Assume.assumeNotNull(messageField);
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, messageField, message);

        Assume.assumeTrue(robot.lookup("#send").tryQuery().isPresent());

        WaitForFXEventsTestHelper.clickOn(robot, "#send");

        var argument = ArgumentCaptor.forClass(FriendRequest.class);
        Mockito.verify(communication, Mockito.times(3)).sendObject(argument.capture());

        Assert.assertThat(argument.getAllValues().get(2).getMessage().getData(), is(message));
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
        MainScreen mainScreen = new MainScreen("Test", communication, stage);
        mainScreen.initCommunication();
        stage.setScene(new Scene(mainScreen));
        stage.show();
    }
}
