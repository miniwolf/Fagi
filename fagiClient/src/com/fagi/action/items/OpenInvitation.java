/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.controller.MainScreen;
import com.fagi.controller.conversation.ConversationController;
import com.fagi.controller.conversation.SendInvitationController;
import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationType;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.network.Communication;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * @author miniwolf
 */
public class OpenInvitation implements Action {
    private final MainScreen mainScreen;
    private final Label username;

    public OpenInvitation(MainScreen mainScreen, Label username) {
        this.mainScreen = mainScreen;
        this.username = username;
    }

    @Override
    public void execute() {
        SendInvitationController controller = new SendInvitationController(mainScreen);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fagi/view/conversation/Invitation.fxml"));
        loader.setController(controller);
        try {
            BorderPane conversationBox = loader.load();
            controller.setUsername(username.getText());
            mainScreen.addElement(conversationBox);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
