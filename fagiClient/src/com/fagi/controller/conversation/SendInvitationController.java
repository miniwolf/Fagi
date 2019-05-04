package com.fagi.controller.conversation;

import com.fagi.action.Action;
import com.fagi.action.ActionContainer;
import com.fagi.action.Actionable;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.LoadHTML;
import com.fagi.action.items.SendInvitation;
import com.fagi.controller.MainScreen;
import com.fagi.model.messages.message.TextMessage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

/**
 * @author miniwolf
 */
public class SendInvitationController extends BorderPane implements ActionContainer {
    @FXML private Label description, header, name;
    @FXML private TextField message;
    @FXML private Button send;

    private MainScreen mainScreen;
    private String username;
    private Actionable actionable = new ActionableImpl();

    public SendInvitationController(MainScreen mainScreen, String username) {
        this.mainScreen = mainScreen;
        new LoadHTML(this, engine, "/com/fagi/view/conversation/Invitation.fxml").execute();
        setUsername(username);
    }

    /**
     * Assign the username to the controller and item.
     * The username is inserted in a default string where the template string is replaced by the
     * username.
     */
    private void setUsername(String username) {
        this.username = username;
        header.setText(header.getText().replace("$", username));
        if (name != null) {
            name.setText(username);
        }
    }

    @FXML
    private void sendMessage() {
        Action action = new SendInvitation(mainScreen.getCommunication(), username,
                                           new TextMessage(message.getText(),
                                                           mainScreen.getUsername(), -1));
        action.execute();
        close();
    }

    @FXML
    private void close() {
        mainScreen.removeElement(this);
    }

    public Actionable getActionable() {
        return actionable;
    }
}
