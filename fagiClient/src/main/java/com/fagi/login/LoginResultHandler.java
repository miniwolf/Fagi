package com.fagi.login;

import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.Response;
import com.fagi.responses.UserOnline;
import javafx.scene.control.Label;

public record LoginResultHandler(Communication communication) {
    public void handle(
            Response response,
            String username,
            Label messageLabel) {
        if (response instanceof AllIsWell) {
            ChatManager
                    .getApplication()
                    .showMainScreen(username, communication);
        } else if (response instanceof FieldEmpty) {
            messageLabel.setText("Fields cannot be empty");
        } else if (response instanceof NoSuchUser) {
            messageLabel.setText("User doesn't exist");
        } else if (response instanceof UserOnline) {
            messageLabel.setText("You are already online");
        } else if (response instanceof PasswordError) {
            messageLabel.setText("Wrong password");
        } else {
            messageLabel.setText("Unknown Exception: " + response.toString());
        }
    }
}
