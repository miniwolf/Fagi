/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.SendInvitationController;

/**
 * @author miniwolf
 */
public class OpenInvitation implements Action<String> {
    private final MainScreen mainScreen;

    public OpenInvitation(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
    }

    @Override
    public void execute(String username) {
        SendInvitationController invitationController = new SendInvitationController(mainScreen, username);
        mainScreen.addElement(invitationController);
    }
}
