package com.fagi.controller.conversation;

import com.fagi.action.items.LoadFXML;
import com.fagi.controller.MainScreen;
import com.fagi.model.DeleteFriendRequest;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.network.Communication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * Created by costa on 11-12-2016.
 */
public class ReceivedInvitationController extends BorderPane {
    @FXML private Label message;
    @FXML private Label username;

    private final FriendRequest request;
    private final MainScreen mainScreen;
    private final Communication communication;

    public ReceivedInvitationController(FriendRequest request, MainScreen mainScreen) {
        this.request = request;
        this.communication = mainScreen.getCommunication();
        this.mainScreen = mainScreen;

        new LoadFXML(this, "/com/fagi/view/conversation/ReceivedInvitation.fxml").execute();
    }

    @FXML
    private void initialize() {
        message.setText(request.getMessage().getData());
        username.setText(request.getFriendUsername());
    }

    @FXML
    private void ignore() {
        communication.sendObject(new DeleteFriendRequest(request.getFriendUsername()));
        close();
    }

    @FXML
    private void accept() {
        communication.sendObject(
            new FriendRequest(request.getMessage().getMessageInfo().getSender(),
                              new TextMessage("Yosh plz!", mainScreen.getUsername(), -1)));
        close();
    }

    private void close() {
        mainScreen.removeElement(this);
    }
}
