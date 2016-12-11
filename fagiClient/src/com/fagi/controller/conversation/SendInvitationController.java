package com.fagi.controller.conversation;

import com.fagi.action.Action;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.SendInvitation;
import com.fagi.controller.MainScreen;
import com.fagi.model.messages.message.TextMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Created by costa on 11-12-2016.
 */
public class SendInvitationController extends ActionableImpl {
    @FXML private Label header, description;
    @FXML private TextField message;
    @FXML private Button send;

    private MainScreen mainScreen;
    private String username;

    public SendInvitationController(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    public void setUsername(String username) {
        this.username = username;
        header.setText(header.getText().replace("$", username));
        description.setText(description.getText().replace("$", username));
    }

    @FXML
    public void sendMessage() {
        Action action = new SendInvitation(mainScreen.getCommunication(), username, new TextMessage(message.getText(), mainScreen.getUsername(), -1));
        action.execute();
    }
}
