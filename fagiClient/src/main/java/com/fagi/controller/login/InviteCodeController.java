/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.action.items.LoadFXML;
import com.fagi.network.ChatManager;
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
        new LoadFXML(this, "/view/login/InviteCode.fxml").execute();
    }

    @FXML
    private void initialize() {
        masterLogin.initialize(inviteCode);
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
