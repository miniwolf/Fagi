/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.handler;

import com.fagi.action.Action;
import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenInvitation;
import com.fagi.controller.MainScreen;

import javafx.scene.control.Label;

import java.util.List;

/**
 * @author miniwolf
 */
public class OpenConversationHandler implements Action {
    private List<String> friendList;
    private MainScreen mainScreen;
    private Label username;

    public OpenConversationHandler(List<String> friendList, MainScreen mainScreen, Label username) {
        this.friendList = friendList;
        this.mainScreen = mainScreen;
        this.username = username;
    }

    @Override
    public void Execute() {
        if ( friendList.contains(username.getText()) ) {
            new OpenConversation(mainScreen, username).Execute();
            // user is a friend find and open conversation or create empty
        } else {
            new OpenInvitation(mainScreen, username).Execute();
            // create invitation
        }
    }
}
