/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.network.ChatManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/**
 * Validate the user details against the server and creates a new user.
 *
 * @author miniwolf
 */
public class InviteCodeController extends DefaultLoginController {
    @FXML private TextField inviteCode;

    public InviteCodeController(MasterLogin masterLogin) {
        this.masterLogin = masterLogin;
    }

    @FXML
    private void initialize() {
        inviteCode.setText(masterLogin.getInviteCode());
        masterLogin.initialize(inviteCode);
        Platform.runLater(() -> inviteCode.getParent().requestFocus());
    }
    @Override
    public void next() {
        if ("".equals(inviteCode.getText())) {
            messageLabel.setText("Invite code cannot be empty");
            return;
        }

        if (createUser()) {
            masterLogin.next();
        }
    }

    private boolean createUser() {
        return ChatManager.handleCreateUser(masterLogin.getUsername(), masterLogin.getPassword(),
                                            messageLabel, inviteCode.getText());
    }
}
