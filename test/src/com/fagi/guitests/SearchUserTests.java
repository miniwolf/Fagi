package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.main.FagiApp;
import com.fagi.model.Friend;
import com.fagi.model.SearchUsersRequest;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.InGoingMessages;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@ExtendWith(ApplicationExtension.class)
public class SearchUserTests {
    private Communication communication;
    private InputHandler inputHandler;

    @Test
    public void WhenWritingInSearchBox_TextIsShownInSearchBox(FxRobot robot) {
        TextField field = robot.lookup("#searchBox").query();

        robot.clickOn(field).write("test");

        Assertions.assertEquals("test", field.getText());
    }

    @Test
    public void WhenAddingANewCharacter_ANewSearchResultIsGenerated(FxRobot robot) {
        robot.clickOn("#searchBox").write("ab");

        var argument = ArgumentCaptor.forClass(SearchUsersRequest.class);
        Mockito.verify(communication, Mockito.times(4)).sendObject(argument.capture());

        Assertions.assertEquals("a", argument.getAllValues().get(2).getSearchString());
        Assertions.assertEquals("ab", argument.getAllValues().get(3).getSearchString());
        Assertions.assertNotEquals(argument.getAllValues().get(2).getSearchString(), argument.getAllValues().get(3).getSearchString());
    }

    @Test
    public void WhenDeletingACharacterInSearchBox_ANewSearchRequestIsGenerated(FxRobot robot) {
        robot.clickOn("#searchBox").write("ab");
        robot.press(KeyCode.BACK_SPACE);

        var argument = ArgumentCaptor.forClass(SearchUsersRequest.class);
        Mockito.verify(communication, Mockito.times(5)).sendObject(argument.capture());

        Assertions.assertEquals("ab", argument.getAllValues().get(3).getSearchString());
        Assertions.assertEquals("a", argument.getAllValues().get(4).getSearchString());
        Assertions.assertNotEquals(argument.getAllValues().get(3).getSearchString(), argument.getAllValues().get(4).getSearchString());
    }

    @Test
    public void WhenHandlingSearchResultWithSingleUser_ShouldShowAMatchingSearchContact(FxRobot robot) {
        var username = "test";
        var usernames = new ArrayList<String>() {{ add(username); }};

        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        Label label = robot.lookup("#userName").query();

        Assertions.assertEquals(username, label.getText());
    }

    @Test
    public void WhenDeletingLastCharacter_SearchShouldClearSearchResultList(FxRobot robot) {
        var username = "a";

        TextField field = robot.lookup("#searchBox").query();
        robot.clickOn(field).write(username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        Label label = robot.lookup("#userName").query();
        Assertions.assertEquals(username, label.getText());

        robot.clickOn(field).press(KeyCode.BACK_SPACE);

        var searchNodes = robot.lookup("#UniqueSearchItem").queryAll();
        Assertions.assertEquals(0, searchNodes.size());
    }

    @Test
    public void WhenDeletingLastCharacter_ReturnToFriendListAsSearchResult(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
            add(new Friend("Friend2", false));
        }};
        inputHandler.addIngoingMessage(new FriendList(new DefaultListAccess<>(friends)));

        robot.clickOn(".contact-button");
        var nodes = robot.lookup("#UniqueContact").queryAll();
        MatcherAssert.assertThat(nodes, hasSize(2));

        var username = "a";

        robot.clickOn("#searchBox").write(username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        robot.press(KeyCode.BACK_SPACE);

        var contactNodes = robot.lookup("#UniqueSearchItem").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(2));
    }

    @Test
    public void UnsuccessfulSearch_ResultsInAnEmptyView(FxRobot robot) {
        robot.clickOn("#searchBox").write("a");

        addIngoingMessageToInputHandler(new SearchUsersResult(new ArrayList<>(), new ArrayList<>()));

        var nodes = robot.lookup("#UniqueSearchItem").queryAll();
        MatcherAssert.assertThat(nodes, hasSize(0));
    }

    @Test
    public void WhenSearchingForUsers_TheResultingNamesMustBeVisible(FxRobot robot) {
        var username1 = "a";
        var username2 = "ab";
        var usernames = new ArrayList<String>() {{ add(username1); add(username2); }};
        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        var contactNodes = new ArrayList<Node>(robot.lookup("#UniqueSearchItem").queryAll());
        Label label1 = robot.from(contactNodes.get(0)).lookup("#userName").query();
        Label label2 = robot.from(contactNodes.get(1)).lookup("#userName").query();

        Assertions.assertTrue(label1.isVisible());
        Assertions.assertTrue(label2.isVisible());
        Assertions.assertEquals(username1, label1.getText());
        Assertions.assertEquals(username2, label2.getText());
    }

    @Test
    public void WhenSearchingForUsers_TheResultingProfilePicturesShouldBeVisible(FxRobot robot) {
        var usernames = new ArrayList<String>() {{ add("a"); add("ab"); }};
        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        var contactNodes = new ArrayList<Node>(robot.lookup("#UniqueSearchItem").queryAll());
        ImageView label1 = robot.from(contactNodes.get(0)).lookup("#image").query();
        ImageView label2 = robot.from(contactNodes.get(1)).lookup("#image").query();

        Assertions.assertTrue(label1.isVisible());
        Assertions.assertTrue(label2.isVisible());
    }

    @Test
    public void UserThatCameFromFriendListStartedSearchingAndThenPressedStopSearching_ShouldReturnToFriendList(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{ add(new Friend("Friend", true)); }};
        addIngoingMessageToInputHandler(new FriendList(new DefaultListAccess<>(friends)));

        robot.clickOn(".contact-button");

        var contactNodes = robot.lookup("#UniqueContact").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(1));

        var username = "a";
        robot.clickOn("#searchBox").write(username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        robot.clickOn("#stopSearchingBtn");

        contactNodes = robot.lookup("#UniqueContact").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(1));
    }

    @Test
    public void UserThatCameFromConversationListStartedSearchingAndThenPressedStopSearching_ShouldReturnToConversationList(FxRobot robot) {
        var username = "a";
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser(username);

        addIngoingMessageToInputHandler(conversation);

        robot.clickOn(".message-button");

        var contactNodes = robot.lookup("#UniqueConversation").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(1));

        robot.clickOn("#searchBox").write(username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        robot.clickOn("#stopSearchingBtn");

        contactNodes = robot.lookup("#UniqueConversation").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(1));
    }

    @Test
    public void WhenClientShowsSearchResults_ItShouldBeInTheOrderThatTheyWereReceived(FxRobot robot) {
        var username1 = "q";
        var username2 = "heja";
        var username3 = "humus";
        var username4 = "borris";
        var username5 = "retsu";
        var usernames = new ArrayList<String>() {{
            add(username1); add(username2); add(username3); add(username4); add(username5);
        }};

        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        var contactNodes = new ArrayList<Node>(robot.lookup("#UniqueSearchItem").queryAll());
        Label label1 = robot.from(contactNodes.get(0)).lookup("#userName").query();
        Label label2 = robot.from(contactNodes.get(1)).lookup("#userName").query();
        Label label3 = robot.from(contactNodes.get(2)).lookup("#userName").query();
        Label label4 = robot.from(contactNodes.get(3)).lookup("#userName").query();
        Label label5 = robot.from(contactNodes.get(4)).lookup("#userName").query();

        Assertions.assertEquals(username1, label1.getText());
        Assertions.assertEquals(username2, label2.getText());
        Assertions.assertEquals(username3, label3.getText());
        Assertions.assertEquals(username4, label4.getText());
        Assertions.assertEquals(username5, label5.getText());
    }

    @Start
    public void start(Stage stage) {
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
        var screen = new MainScreen("Test", communication, stage);
        screen.initCommunication();
        stage.setScene(new Scene(screen));
        stage.show();
    }

    private void addIngoingMessageToInputHandler(InGoingMessages msg) {
        inputHandler.addIngoingMessage(msg);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
