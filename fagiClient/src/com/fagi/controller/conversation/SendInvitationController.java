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
import javafx.scene.layout.BorderPane;

import javax.swing.border.Border;

/**
 * @author miniwolf
 */
public class SendInvitationController extends ActionableImpl {
    @FXML private Label header, description;
    @FXML private TextField message;
    @FXML private Button send;
    @FXML private BorderPane body;
    @FXML private Label name;

    private MainScreen mainScreen;
    private String username;

    public SendInvitationController(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    /**
     * Assign the username to the controller and item.
     * The username is inserted in a default string where the template string is replaced by the
     * username.
     * @param username username of the username you want to invite.
     */
    public void setUsername(String username) {
        this.username = username;
        header.setText(header.getText().replace("$", username));
        name.setText(username);
    }

    @FXML
    public void sendMessage() {
        Action action = new SendInvitation(mainScreen.getCommunication(), username, new TextMessage(message.getText(), mainScreen.getUsername(), -1));
        action.execute();
    }

    @FXML
    public void close() {
        mainScreen.removeElement(body);
    }
}
