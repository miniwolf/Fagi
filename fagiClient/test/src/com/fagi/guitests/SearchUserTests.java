package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
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
