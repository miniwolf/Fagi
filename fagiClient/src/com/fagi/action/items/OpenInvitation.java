/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.SendInvitationController;
import javafx.scene.control.Label;

/**
 * @author miniwolf
 */
public class OpenInvitation implements Action {
    private final MainScreen mainScreen;
    private final String username;

    public OpenInvitation(MainScreen mainScreen, String username) {
        this.mainScreen = mainScreen;
        this.username = username;
    }

    @Override
    public void execute() {
        SendInvitationController controller = new SendInvitationController(mainScreen, username);
        mainScreen.addElement(controller);
    }
}
