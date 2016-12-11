package com.fagi.controller.conversation;

import com.fagi.action.ActionableImpl;
import com.fagi.model.FriendRequest;
import com.fagi.network.Communication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by costa on 11-12-2016.
 */
public class ReceivedInvitationController extends ActionableImpl {
    @FXML private Label message;
    @FXML private Label username;

    private final FriendRequest request;
    private final Communication communication;

    public ReceivedInvitationController(FriendRequest request, Communication communication) {
        this.request = request;
        this.communication = communication;
    }

    @FXML
    public void initialize() {
        message.setText(request.getMessage().getData());
        username.setText(request.getFriendUsername());
    }

    @FXML
    public void ignore() {

    }

    @FXML
    public void accept() {

    }
}
