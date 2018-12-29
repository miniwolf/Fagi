package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import com.fagi.model.Friend;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

@ExtendWith(ApplicationExtension.class)
public class FriendsTests {
    private InputHandler inputHandler;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting FriendsTests tests");
    }

    @Test
    public void receivingFriendListFromServer_FriendIsVisibleOnContent(FxRobot robot) {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", true));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);
        Assertions.assertFalse(
                robot.lookup("#UniqueContact").tryQuery().isPresent(),
                "Should not find friend item before changing the content list to contacts");

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        Node contactsButton = robot.lookup(".contact-button").query();
        Assertions.assertNotNull(contactsButton);
        robot.clickOn(contactsButton);

        Node contactNode = robot.lookup("#UniqueContact").query();
        Assertions.assertNotNull(contactNode);
        Label nameLabel = robot.lookup("#userName").query();
        Assertions.assertEquals("Friend", nameLabel.getText());
        Set<ImageView> imageView = robot.lookup("#image").queryAll();
        MatcherAssert.assertThat(imageView, hasSize(1));

        var o = (Image) ((ImageView) imageView.toArray()[0]).getImage();
        Assertions.assertTrue(o.getUrl().contains("F.png"));

        Node query = robot.lookup("#status").query();
        MatcherAssert.assertThat(query.getStyleClass(), containsInAnyOrder("pD", "flaeQ"));
    }

    @Test
    public void receivingFriendListFromServer_OfflineFriendGuiSetup(FxRobot robot) {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", false));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);

        robot.clickOn((Node) robot.lookup(".contact-button").query());

        Label nameLabel = robot.lookup("#userName").query();
        Assertions.assertEquals("Friend", nameLabel.getText());

        Node query = robot.lookup("#status").query();
        MatcherAssert.assertThat(query.getStyleClass(), not(contains("pD")));
    }

    @Test
    public void receivingFriendListFromServer_AllFriendsAreVisibleOnContent(FxRobot robot) {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", true));
        friends.add(new Friend("Friend2", true));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        Node contactsButton = robot.lookup(".contact-button").query();
        Assertions.assertNotNull(contactsButton);
        robot.clickOn(contactsButton);

        Set<Node> contactNodes = robot.lookup("#UniqueContact").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(2));

        List<String> collect = robot.lookup("#userName").queryAll().stream()
                                                  .map(node -> ((Label) node).getText())
                                                  .collect(Collectors.toList());

        MatcherAssert.assertThat(collect, containsInAnyOrder("Friend", "Friend2"));
    }

    @Test
    public void receivingFriendListFromServer_FriendsArePostedInAlphabeticalOrder(FxRobot robot) throws InterruptedException {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("AFriend", true));
        friends.add(new Friend("CFriend", true));
        friends.add(new Friend("BFriend", true));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        robot.clickOn((Node) robot.lookup(".contact-button").query());
        List<String> collect = robot.lookup("#userName").queryAll().stream()
                                                  .map(node -> ((Label) node).getText())
                                                  .collect(Collectors.toList());

        // Contains will check the order, containsInAnyOrder for other test
        MatcherAssert.assertThat(collect, contains("AFriend", "BFriend", "CFriend"));
    }

    @Test
    public void receivingFriendListFromServer_OfflineFriendsAreInList(FxRobot robot) {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", true));
        friends.add(new Friend("Friend2", false));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        robot.clickOn((Node) robot.lookup(".contact-button").query());
        Set<Node> contactNodes = robot.lookup("#UniqueContact").queryAll();
        MatcherAssert.assertThat(contactNodes, hasSize(2));
    }

    @Start
    public void start(Stage stage) {
        Draggable draggable = new Draggable(stage);

        Communication communication = Mockito.mock(Communication.class);
        inputHandler = Mockito.mock(InputHandler.class);
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Mockito.doCallRealMethod().when(communication).setInputHandler(inputHandler);
        Mockito.doCallRealMethod().when(inputHandler).setupDistributor();
        Mockito.doCallRealMethod().when(inputHandler).addIngoingMessage(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> new MasterLogin(fagiApp, communication, stage, draggable))
               .when(fagiApp).showLoginScreen();

        Thread inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor();

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        MainScreen test = new MainScreen("Test", communication, stage);
        test.initCommunication();
        stage.setScene(new Scene(test));
        stage.show();
    }
}
