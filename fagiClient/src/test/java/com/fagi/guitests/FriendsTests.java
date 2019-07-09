package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.helpers.WaitForFXEventsTestHelper;
import com.fagi.testfxExtension.FagiNodeFinderImpl;
import com.fagi.main.FagiApp;
import com.fagi.model.Friend;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxAssert;
import org.testfx.api.FxRobot;
import org.testfx.api.FxService;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.base.StyleableMatchers;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fagi.helpers.WaitForFXEventsTestHelper.addIngoingMessageToInputHandler;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

@ExtendWith(ApplicationExtension.class)
public class FriendsTests {
    private InputHandler inputHandler;

    @BeforeAll
    public static void initialize() {
        System.out.println("Starting FriendsTests tests");
        FxAssert.assertContext().setNodeFinder(new FagiNodeFinderImpl(FxService.serviceContext().getWindowFinder()));
    }

    @Test
    public void receivingFriendListFromServer_FriendIsVisibleOnContent(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{ add(new Friend("Friend", true)); }};
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)));
        Assertions.assertFalse(
                robot.lookup("#UniqueContact").tryQuery().isPresent(),
                "Should not find friend item before changing the content list to contacts");

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");
        Assertions.assertTrue(
                robot.lookup("#UniqueContact").tryQuery().isPresent(),
                "Cannot see friend item after switching the content list to contacts.");

        Label nameLabel = robot.lookup("#userName").query();
        Assertions.assertEquals("Friend", nameLabel.getText());
        Set<ImageView> imageView = robot.lookup("#image").queryAll();
        MatcherAssert.assertThat(imageView, hasSize(1));

        var image = ((ImageView) imageView.toArray()[0]).getImage();
        Assertions.assertTrue(image.getUrl().contains("F.png"));

        var status = robot.lookup("#status").query();
        MatcherAssert.assertThat(status.getStyleClass(), containsInAnyOrder("pD", "flaeQ"));
    }

    @Test
    public void receivingFriendListFromServer_OfflineFriendGuiSetup(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{ add(new Friend("Friend", false)); }};
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)));

        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");

        FxAssert.verifyThat(
                "#userName",
                LabeledMatchers.hasText("Friend")
        );

        FxAssert.verifyThat(
                "#status",
                StyleableMatchers.doesntContainsClass("pD")
        );
    }

    @Test
    public void receivingFriendListFromServer_AllFriendsAreVisibleOnContent(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
            add(new Friend("Friend2", true));
        }};
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)), friends.size());

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");

        FxAssert.verifyThatIter(
                "#UniqueContact",
                IsIterableWithSize.iterableWithSize(2)
        );

        var collect = robot.lookup("#userName").queryAll().stream()
                                                  .map(node -> ((Label) node).getText())
                                                  .collect(Collectors.toList());

        MatcherAssert.assertThat(collect, containsInAnyOrder("Friend", "Friend2"));
    }

    @Test
    public void receivingFriendListFromServer_FriendsArePostedInAlphabeticalOrder(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("AFriend", true));
            add(new Friend("CFriend", true));
            add(new Friend("BFriend", true));
        }};
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)), friends.size());

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");
        var collect = robot.lookup("#userName").queryAll().stream()
                                                  .map(node -> ((Label) node).getText())
                                                  .collect(Collectors.toList());

        // Contains will check the order, containsInAnyOrder for other test
        MatcherAssert.assertThat(collect, contains("AFriend", "BFriend", "CFriend"));
    }

    @Test
    public void receivingFriendListFromServer_OfflineFriendsAreInList(FxRobot robot) {
        List<Friend> friends = new ArrayList<>() {{
            add(new Friend("Friend", true));
            add(new Friend("Friend2", false));
        }};
        WaitForFXEventsTestHelper.addIngoingMessageToInputHandler(inputHandler, new FriendList(new DefaultListAccess<>(friends)), friends.size());

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        WaitForFXEventsTestHelper.clickOn(robot, ".contact-button");

        FxAssert.verifyThatIter(
                "#UniqueContact",
                IsIterableWithSize.iterableWithSize(2)
        );
    }

    @Start
    public void start(Stage stage) {
        var draggable = new Draggable(stage);

        var communication = Mockito.mock(Communication.class);
        inputHandler = Mockito.mock(InputHandler.class);
        var fagiApp = Mockito.mock(FagiApp.class);
        Mockito.doCallRealMethod().when(communication).setInputHandler(inputHandler);
        Mockito.doCallRealMethod().when(inputHandler).setupDistributor();
        Mockito.doCallRealMethod().when(inputHandler).addIngoingMessage(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> new MasterLogin(fagiApp, communication, stage, draggable))
               .when(fagiApp).showLoginScreen();

        var inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();

        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor();

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        stage.setScene(new Scene(new AnchorPane()));
        var test = new MainScreen("Test", communication, stage);
        test.initCommunication();
        stage.setScene(new Scene(test));
        stage.show();
    }
}
