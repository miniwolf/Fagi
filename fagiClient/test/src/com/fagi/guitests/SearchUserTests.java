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
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testfx.framework.junit.ApplicationTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;


public class SearchUserTests extends ApplicationTest {
    private Communication communication;
    private Communication comspy;
    private InputHandler inputHandler;
    private MainScreen screen;

    @Test
    public void WhenWritingInSearchBox_TextIsShownInSearchBox() {
        TextField field = lookup("#searchBox").query();

        clickOn(field).write("test");

        Assert.assertEquals(field.getText(), "test");
    }

    @Test
    public void WhenAddingANewCharacter_ANewSearchResultIsGenerated() {
        TextField field = lookup("#searchBox").query();

        clickOn(field).write("ab");

        ArgumentCaptor<SearchUsersRequest> argument = ArgumentCaptor.forClass(SearchUsersRequest.class);
        Mockito.verify(communication, Mockito.times(4)).sendObject(argument.capture());

        Assert.assertEquals("a", argument.getAllValues().get(2).getSearchString());
        Assert.assertEquals("ab", argument.getAllValues().get(3).getSearchString());
        Assert.assertNotEquals(argument.getAllValues().get(2).getSearchString(), argument.getAllValues().get(3).getSearchString());
    }

    @Test
    public void WhenDeletingACharacterInSearchBox_ANewSearchRequestIsGenerated() {
        TextField field = lookup("#searchBox").query();

        clickOn(field).write("ab");
        press(KeyCode.BACK_SPACE);

        ArgumentCaptor<SearchUsersRequest> argument = ArgumentCaptor.forClass(SearchUsersRequest.class);
        Mockito.verify(communication, Mockito.times(5)).sendObject(argument.capture());

        Assert.assertEquals("ab", argument.getAllValues().get(3).getSearchString());
        Assert.assertEquals("a", argument.getAllValues().get(4).getSearchString());
        Assert.assertNotEquals(argument.getAllValues().get(3).getSearchString(), argument.getAllValues().get(4).getSearchString());
    }

    @Test
    public void WhenHandlingSearchResultWithSingleUser_ShouldShowAMatchingSearchContact() {
        var username = "test";
        var usernames = new ArrayList<String>();
        usernames.add(username);

        var result = new SearchUsersResult(usernames, new ArrayList<>());

        addIngoingMessageToInputHandler(result);

        var label = lookup("#userName").query();

        Assert.assertEquals(username, ((Label) label).getText());
    }

    @Test
    public void WhenDeletingLastCharacter_SearchShouldClearSearchResultList() {
        var username = "a";

        TextField field = lookup("#searchBox").query();

        clickOn(field).write(username);

        var usernames = new ArrayList<String>();
        usernames.add(username);

        var result = new SearchUsersResult(usernames, new ArrayList<>());

        addIngoingMessageToInputHandler(result);

        var label = lookup("#userName").query();

        Assert.assertEquals(username, ((Label) label).getText());

        clickOn(field).press(KeyCode.BACK_SPACE);

        var searchNodes = lookup("#UniqueSearchItem").queryAll();

        Assert.assertEquals(0, searchNodes.size());
    }

    @Test
    public void WhenDeletingLastCharacter_ReturnToFriendListAsSearchResult() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", true));
        friends.add(new Friend("Friend2", false));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);

        clickOn((Node) lookup(".contact-button").query());
        Set<Node> nodes = lookup("#UniqueContact").queryAll();
        Assert.assertThat(nodes, hasSize(2));

        var username = "a";
        var usernames = new ArrayList<String>();

        TextField field = lookup("#searchBox").query();

        clickOn(field).write(username);

        usernames.add(username);

        var result = new SearchUsersResult(usernames, new ArrayList<>());

        addIngoingMessageToInputHandler(result);

        press(KeyCode.BACK_SPACE);

        Set<Node> contactNodes = lookup("#UniqueSearchItem").queryAll();
        Assert.assertThat(contactNodes, hasSize(2));
    }

    @Test
    public void UnsuccessfulSearch_ResultsInAnEmptyView() {
        var username = "a";
        var result = new SearchUsersResult(new ArrayList<>(), new ArrayList<>());

        var field = lookup("#searchBox").query();
        clickOn(field).write(username);

        addIngoingMessageToInputHandler(result);

        var nodes = lookup("#UniqueSearchItem").queryAll();

        Assert.assertThat(nodes, hasSize(0));
    }

    @Test
    public void WhenSearchingForUsers_TheResultingNamesMustBeVisible() {
        var username1 = "a";
        var username2 = "ab";
        var usernames = new ArrayList<String>();
        usernames.add(username1);
        usernames.add(username2);

        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        ArrayList<Node> contactNodes = new ArrayList<>(lookup("#UniqueSearchItem").queryAll());
        Label label1 = from(contactNodes.get(0)).lookup("#userName").query();
        Label label2 = from(contactNodes.get(1)).lookup("#userName").query();

        Assert.assertTrue(label1.isVisible());
        Assert.assertTrue(label2.isVisible());
        Assert.assertEquals(username1, label1.getText());
        Assert.assertEquals(username2, label2.getText());
    }

    @Test
    public void WhenSearchingForUsers_TheResultingProfilePicturesShouldBeVisible() {
        var username1 = "a";
        var username2 = "ab";
        var usernames = new ArrayList<String>();
        usernames.add(username1);
        usernames.add(username2);

        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        ArrayList<Node> contactNodes = new ArrayList<>(lookup("#UniqueSearchItem").queryAll());
        ImageView label1 = from(contactNodes.get(0)).lookup("#image").query();
        ImageView label2 = from(contactNodes.get(1)).lookup("#image").query();

        Assert.assertTrue(label1.isVisible());
        Assert.assertTrue(label2.isVisible());
    }

    @Test
    public void UserThatCameFromFriendListStartedSearchingAndThenPressedStopSearching_ShouldReturnToFriendList() {
        var username = "a";
        var usernames = new ArrayList<String>();
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", true));

        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        addIngoingMessageToInputHandler(friendList);

        clickOn((Node) lookup(".contact-button").query());

        Set<Node> contactNodes = lookup("#UniqueContact").queryAll();
        Assert.assertThat(contactNodes, hasSize(1));

        TextField field = lookup("#searchBox").query();

        clickOn(field).write(username);

        usernames.add(username);

        var result = new SearchUsersResult(usernames, new ArrayList<>());

        addIngoingMessageToInputHandler(result);

        clickOn((Node) lookup("#stopSearchingBtn").query());

        contactNodes = lookup("#UniqueContact").queryAll();
        Assert.assertThat(contactNodes, hasSize(1));
    }

    @Test
    public void UserThatCameFromConversationListStartedSearchingAndThenPressedStopSearching_ShouldReturnToConversationList() {
        var username = "a";
        var usernames = new ArrayList<String>();
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser(username);

        addIngoingMessageToInputHandler(conversation);

        clickOn((Node) lookup(".message-button").query());

        Set<Node> contactNodes = lookup("#UniqueConversation").queryAll();
        Assert.assertThat(contactNodes, hasSize(1));

        TextField field = lookup("#searchBox").query();

        clickOn(field).write(username);

        usernames.add(username);

        var result = new SearchUsersResult(usernames, new ArrayList<>());

        addIngoingMessageToInputHandler(result);

        clickOn((Node) lookup("#stopSearchingBtn").query());

        contactNodes = lookup("#UniqueConversation").queryAll();

        Assert.assertThat(contactNodes, hasSize(1));
    }

    @Test
    public void WhenClientShowsSearchResults_ItShouldBeInTheOrderThatTheyWereReceived() {
        var username1 = "q";
        var username2 = "heja";
        var username3 = "humus";
        var username4 = "borris";
        var username5 = "retsu";

        var usernames = new ArrayList<String>();
        usernames.add(username1);
        usernames.add(username2);
        usernames.add(username3);
        usernames.add(username4);
        usernames.add(username5);

        addIngoingMessageToInputHandler(new SearchUsersResult(usernames, new ArrayList<>()));

        ArrayList<Node> contactNodes = new ArrayList<>(lookup("#UniqueSearchItem").queryAll());
        Label label1 = from(contactNodes.get(0)).lookup("#userName").query();
        Label label2 = from(contactNodes.get(1)).lookup("#userName").query();
        Label label3 = from(contactNodes.get(2)).lookup("#userName").query();
        Label label4 = from(contactNodes.get(3)).lookup("#userName").query();
        Label label5 = from(contactNodes.get(4)).lookup("#userName").query();

        Assert.assertEquals(username1, label1.getText());
        Assert.assertEquals(username2, label2.getText());
        Assert.assertEquals(username3, label3.getText());
        Assert.assertEquals(username4, label4.getText());
        Assert.assertEquals(username5, label5.getText());
    }

    @Override
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

        comspy = Mockito.spy(communication);
        Mockito.doNothing().when(comspy).sendObject(Mockito.any());

        Thread inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor();

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        screen = new MainScreen("Test", communication, stage);
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
