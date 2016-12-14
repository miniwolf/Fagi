package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ReceivedInvitationController;
import com.fagi.model.FriendRequest;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Created by costa on 11-12-2016.
 */
public class OpenReceivedFriendRequest implements Action {
    private final MainScreen mainScreen;
    private final FriendRequest request;


    public OpenReceivedFriendRequest(MainScreen mainScreen, FriendRequest request) {
        this.mainScreen = mainScreen;
        this.request = request;
    }

    @Override
    public void execute() {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/fagi/view/conversation/ReceivedInvitation.fxml"));
        ReceivedInvitationController controller =
                new ReceivedInvitationController(request, mainScreen);
        loader.setController(controller);
        try {
            BorderPane conversationBox = loader.load();
            mainScreen.addElement(conversationBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
