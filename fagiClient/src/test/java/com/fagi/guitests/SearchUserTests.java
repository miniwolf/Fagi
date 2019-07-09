package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.main.FagiApp;
import com.fagi.model.Friend;
import com.fagi.model.SearchUsersRequest;
import com.fagi.model.SearchUsersResult;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hamcrest.collection.IsIterableWithSize;
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
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.util.ArrayList;
import java.util.List;

import static com.fagi.helpers.WaitForFXEventsTestHelper.addIngoingMessageToInputHandler;

@ExtendWith(ApplicationExtension.class)
public class SearchUserTests {
    private Communication communication;
    private InputHandler inputHandler;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting search user tests");
        FxAssert.assertContext().setNodeFinder(new FagiNodeFinderImpl(FxService.serviceContext().getWindowFinder()));
    }

    @Test
    public void WhenWritingInSearchBox_TextIsShownInSearchBox(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", "test");

        FxAssert.verifyThat(
                "#searchBox",
                TextInputControlMatchers.hasText("test")
        );
    }

    @Test
    public void WhenAddingANewCharacter_ANewSearchResultIsGenerated(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", "ab");

        var argument = ArgumentCaptor.forClass(SearchUsersRequest.class);
        Mockito.verify(communication, Mockito.times(4)).sendObject(argument.capture());

        Assertions.assertAll(
                () -> Assertions.assertEquals("a", argument.getAllValues().get(2).getSearchString()),
                () -> Assertions.assertEquals("ab", argument.getAllValues().get(3).getSearchString()),
                () -> Assertions.assertNotEquals(argument.getAllValues().get(2).getSearchString(), argument.getAllValues().get(3).getSearchString())
        );
    }

    @Test
    public void WhenDeletingACharacterInSearchBox_ANewSearchRequestIsGenerated(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", "ab");
        robot.press(KeyCode.BACK_SPACE);

        var argument = ArgumentCaptor.forClass(SearchUsersRequest.class);
        Mockito.verify(communication, Mockito.times(5)).sendObject(argument.capture());

        Assertions.assertAll(
                () -> Assertions.assertEquals("ab", argument.getAllValues().get(3).getSearchString()),
                () -> Assertions.assertEquals("a", argument.getAllValues().get(4).getSearchString()),
                () -> Assertions.assertNotEquals(argument.getAllValues().get(3).getSearchString(), argument.getAllValues().get(4).getSearchString())
        );
    }

    @Test
    public void WhenHandlingSearchResultWithSingleUser_ShouldShowAMatchingSearchContact() {
        var username = "test";
        var usernames = new ArrayList<String>() {{ add(username); }};

        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        FxAssert.verifyThat(
                "#userName",
                LabeledMatchers.hasText(username)
        );
    }

    @Test
    public void WhenDeletingLastCharacter_SearchShouldClearSearchResultList(FxRobot robot) {
        var username = "a";

        TextField field = robot.lookup("#searchBox").query();
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, field, username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        FxAssert.verifyThat(
                "#userName",
                LabeledMatchers.hasText(username)
        );

        robot.clickOn(field).press(KeyCode.BACK_SPACE);

        FxAssert.verifyThatIter(
                "#UniqueSearchContact",
                IsIterableWithSize.iterableWithSize(0)
        );
    }

    @Test
    public void WhenDeletingLastCharacter_ReturnToFriendListAsSearchResult(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
            add(new Friend("Friend2", false));
        }};
        addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)), friends.size());

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");

        FxAssert.verifyThatIter(
                "#UniqueContact",
                IsIterableWithSize.iterableWithSize(2)
        );

        var username = "a";

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        robot.press(KeyCode.BACK_SPACE);

        FxAssert.verifyThatIter(
                "#UniqueSearchContact",
                IsIterableWithSize.iterableWithSize(2)
        );
    }

    @Test
    public void UnsuccessfulSearch_ResultsInAnEmptyView(FxRobot robot) {
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", "a");

        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(new ArrayList<>(), new ArrayList<>()));

        FxAssert.verifyThatIter(
                "#UniqueSearchContact",
                IsIterableWithSize.iterableWithSize(0)
        );
    }

    @Test
    public void WhenSearchingForUsers_TheResultingNamesMustBeVisible(FxRobot robot) {
        var username1 = "a";
        var username2 = "ab";
        var usernames = new ArrayList<String>() {{ add(username1); add(username2); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()), usernames.size());

        var contactNodes = new ArrayList<Node>(robot.lookup("#UniqueSearchContact").queryAll());
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
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()), 2);

        var contactNodes = new ArrayList<Node>(robot.lookup("#UniqueSearchContact").queryAll());
        ImageView label1 = robot.from(contactNodes.get(0)).lookup("#image").query();
        ImageView label2 = robot.from(contactNodes.get(1)).lookup("#image").query();

        Assertions.assertTrue(label1.isVisible());
        Assertions.assertTrue(label2.isVisible());
    }

    @Test
    public void UserThatCameFromFriendListStartedSearchingAndThenPressedStopSearching_ShouldReturnToFriendList(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{ add(new Friend("Friend", true)); }};
        addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)));

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");

        FxAssert.verifyThatIter(
                "#UniqueContact",
                IsIterableWithSize.iterableWithSize(1)
        );

        var username = "a";
        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        WaitForFXEventsTestHelper.clickOn(robot, "#stopSearchingBtn");

        FxAssert.verifyThatIter(
                "#UniqueContact",
                IsIterableWithSize.iterableWithSize(1)
        );
    }

    @Test
    public void UserThatCameFromConversationListStartedSearchingAndThenPressedStopSearching_ShouldReturnToConversationList(FxRobot robot) {
        var username = "a";
        var conversation = new Conversation(1, "test", ConversationType.Placeholder);
        conversation.addUser(username);

        addIngoingMessageToInputHandler(inputHandler, conversation);

        WaitForFXEventsTestHelper.clickOn(robot, ".message-button");

        FxAssert.verifyThatIter(
                "#UniqueConversationItem",
                IsIterableWithSize.iterableWithSize(1)
        );

        WaitForFXEventsTestHelper.clickOnAndWrite(robot, "#searchBox", username);

        var usernames = new ArrayList<String>() {{ add(username); }};
        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()));

        WaitForFXEventsTestHelper.clickOn(robot, "#stopSearchingBtn");

        FxAssert.verifyThatIter(
                "#UniqueConversationItem",
                IsIterableWithSize.iterableWithSize(1)
        );
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

        addIngoingMessageToInputHandler(inputHandler, new SearchUsersResult(usernames, new ArrayList<>()), usernames.size());

        var contactNodes = new ArrayList<Node>(robot.lookup("#UniqueSearchContact").queryAll());
        Label label1 = robot.from(contactNodes.get(0)).lookup("#userName").query();
        Label label2 = robot.from(contactNodes.get(1)).lookup("#userName").query();
        Label label3 = robot.from(contactNodes.get(2)).lookup("#userName").query();
        Label label4 = robot.from(contactNodes.get(3)).lookup("#userName").query();
        Label label5 = robot.from(contactNodes.get(4)).lookup("#userName").query();

        Assertions.assertAll(
                () -> FxAssert.verifyThat(label1, LabeledMatchers.hasText(username1)),
                () -> FxAssert.verifyThat(label2, LabeledMatchers.hasText(username2)),
                () -> FxAssert.verifyThat(label3, LabeledMatchers.hasText(username3)),
                () -> FxAssert.verifyThat(label4, LabeledMatchers.hasText(username4)),
                () -> FxAssert.verifyThat(label5, LabeledMatchers.hasText(username5))
        );
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
}
