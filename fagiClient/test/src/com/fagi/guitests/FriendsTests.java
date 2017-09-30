package com.fagi.guitests;

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.main.FagiApp;
import com.fagi.model.Friend;
import com.fagi.model.messages.lists.DefaultListAccess;
import com.fagi.model.messages.lists.FriendList;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.network.InputHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.loadui.testfx.GuiTest;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class FriendsTests extends GuiTest {
    private InputHandler inputHandler;

    @Test
    public void Test() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend("Friend", true));
        FriendList friendList = new FriendList(new DefaultListAccess<>(friends));
        inputHandler.addIngoingMessage(friendList);
        Assert.assertNull(
                "Should not find friend item before changing the content list to contacts",
                lookup("#UniqueContact").query());

        // Make sure that we are on the contact list.
        // This might be default, be we cannot verify this as a feature
        Node contactsButton = lookup(".contact-button").query();
        Assert.assertNotNull(contactsButton);
        clickOn(contactsButton);

        Node contactNode = lookup("#UniqueContact").query();
        Assert.assertNotNull(contactNode);
        Label nameLabel = lookup("#userName").query();
        Assert.assertEquals("Friend", nameLabel.getText());
    }

    @Override
    protected Parent getRootNode() {
        System.out.println("Starting FriendsTests tests");
        Stage stage = (Stage) targetWindow();
        stage.setScene(new Scene(new AnchorPane()));

        Communication communication = Mockito.mock(Communication.class);
        inputHandler = Mockito.mock(InputHandler.class);
        FagiApp fagiApp = Mockito.mock(FagiApp.class);
        Mockito.doCallRealMethod().when(communication).setInputHandler(inputHandler);
        Mockito.doCallRealMethod().when(inputHandler).setupDistributor();
        Mockito.doCallRealMethod().when(inputHandler).addIngoingMessage(Mockito.any());
        Mockito.doAnswer(invocationOnMock -> new MasterLogin(fagiApp, "config/serverinfo.config",
                                                             stage, stage.getScene()))
               .when(fagiApp).showLoginScreen();

        Thread inputThread = new Thread(inputHandler);
        inputThread.setDaemon(true);
        inputThread.start();
        communication.setInputHandler(inputHandler);
        inputHandler.setupDistributor();

        ChatManager.setCommunication(communication);
        ChatManager.setApplication(fagiApp);

        MainScreen test = new MainScreen("Test", communication, stage);
        test.initCommunication();
        return test;
    }
}
