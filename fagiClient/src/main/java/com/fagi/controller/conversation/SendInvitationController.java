package com.fagi.controller.conversation;

import com.fagi.action.Action;
import com.fagi.action.items.LoadFXML;
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
public class SendInvitationController extends BorderPane {
    @FXML private Label description, header, name;
    @FXML private TextField message;
    @FXML private Button send;

    private MainScreen mainScreen;
    private Action<TextMessage> action;

    public SendInvitationController(MainScreen mainScreen, String username) {
        this.mainScreen = mainScreen;
        this.action = new SendInvitation(mainScreen.getCommunication(), username);

        new LoadFXML("/view/conversation/Invitation.fxml").execute(this);
        setUsername(username);
    }

    /**
     * Assign the username to the controller and item.
     * The username is inserted in a default string where the template string is replaced by the
     * username.
     */
    private void setUsername(String username) {
        header.setText(header.getText().replace("$", username));
        if (name != null) {
            name.setText(username);
        }
    }

    @FXML
    private void sendMessage() {
        action.execute(new TextMessage(
                message.getText(),
                mainScreen.getUsername(),
                -1));
        close();
    }

    @FXML
    private void close() {
        mainScreen.removeElement(this);
    }
}
